package com.example.picbooker.media;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import jakarta.persistence.EntityManager;

@Service
public class MediaService { // to think rename to media service

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PhotographerService photographerService;

    private final Storage storage;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @Value("${app.portfolio-limit}")
    private Integer portfolioLimit;

    public MediaService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file, MediaType mediaType) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        Blob blob = storage.create(blobInfo, file.getBytes());
        // to make files public
        blob.toBuilder().setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))).build()
                .update();

        // return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
        return blob.getMediaLink(); // Returns a publicly accessible URL

    }

    @Transactional
    private Boolean deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            fileName = fileName.split("\\?")[0]; // Remove query parameters
            System.out.println("Extracted file name: " + fileName); // Debugging

            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                System.out.println("File deleted successfully: " + fileName);
                return true;
            } else {
                System.out.println("File not found or could not be deleted: " + fileName);
                return false;
            }
        } catch (StorageException e) {
            System.err.println("Error deleting file from Firebase Storage: " + e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file from Firebase Storage");
        }
    }

    public Media findByIdThrow(Long mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Media not found"));

    }

    public long getCountForPhotographer(Long photographerId) {
        // to think of making sure profile pic not in count
        return mediaRepository.countByPhotographer_Id(photographerId);
    }

    @Transactional
    public Media create(Photographer photographer, String url, String description, MediaType type) {
        return new Media(null, photographer, url, description, type);
    }

    @Transactional
    public Media createAndSave(Photographer photographer, String url, String description, MediaType type) {

        return save(create(photographer, url, description, type));
    }

    public Media save(Media media) {

        return (mediaRepository.save(media));
    }

    public Page<Media> getGalleryForPhotographer(Pageable page, Long photographerId) {
        return mediaRepository.findPortfolioByPhotographer(photographerId, page);

    }

    @Transactional
    public void delete(long id, Long photographerId) {
        try {
            Media media = findByIdThrow(id);
            Photographer photographer = media.getPhotographer();
            if (photographer.getId() != photographerId) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }
            deleteFile(media.getMediaUrl());
            photographer.getMediaUploads().remove(media);
            photographerService.save(photographer);
            mediaRepository.deleteById(id);
            mediaRepository.flush();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong during deletion");
        }

    }

    @Transactional
    void delete(Media media, Photographer photographer) {
        // media = findByIdThrow(media.getId()) ;
        if (!media.getPhotographer().getId().equals(photographer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
        }

        try {
            // 1. Delete from Firebase first
            boolean firebaseDeleted = deleteFile(media.getMediaUrl());
            if (!firebaseDeleted) {
                System.out.println("Failed to delete from storage");
                // throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete
                // from storage");
            }

            // 2. Remove from photographer's collection
            photographer.getMediaUploads().removeIf(m -> m.getId().equals(media.getId()));

            // 3. Delete from database
            mediaRepository.delete(media);
            mediaRepository.flush(); // Force immediate execution

            // 4. Verify deletion
            if (mediaRepository.existsById(media.getId())) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete media record");
            }

        } catch (Exception e) {
            // log.error("Failed to delete media {}: {}", media.getId(), e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to complete deletion: " + e.getMessage());
        }

    }

    @Transactional
    public Media updateDescription(long id, Long photographerId, String description) {
        try {
            Media media = findByIdThrow(id);
            Photographer photographer = media.getPhotographer();
            if (photographer.getId() != photographerId) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
            }

            media.setDescription(description);
            return save(media);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong during deletion");
        }

    }

    public Media uploadMediaForPhotographerGallery(Photographer photographer, MultipartFile file,
            MediaType type, String description) {
        try {

            String url = uploadFile(file, type);
            return createAndSave(photographer, url, description, type);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to convert or upload file");
        }
    }

    @Transactional
    public void deleteProfilePicture(Photographer photographer) {
        try {
            Media previousPhoto = getProfilePicturePhotographer(photographer.getId());

            if (previousPhoto != null) {
                delete(previousPhoto.getId(), photographer.getId());
                photographer.setProfilePhotoUrl(null); // update URL directly
                photographerService.save(photographer); // ensure this persists

            }

        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to convert or upload file");
        }
    }

    @Transactional
    public Media uploadProfilePicture(Photographer photographer, MultipartFile file) {
        try {
            Media previousPhoto = getProfilePicturePhotographer(photographer.getId());
            String url = uploadFile(file, MediaType.PROFILE_PICTURE);

            if (previousPhoto != null) {
                System.out.println("going to delete...");
                deleteFile(previousPhoto.getMediaUrl());
                previousPhoto.setPhotographer(null);
                photographer.getMediaUploads().removeIf(m -> m.getId().equals(previousPhoto.getId()));
                photographer.setProfilePhotoUrl(url);
                photographerService.save(photographer);

                mediaRepository.delete(previousPhoto);
                mediaRepository.flush();

                // 4. Verify deletion
                if (mediaRepository.existsById(previousPhoto.getId())) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete media record");
                }
                System.out.println("After verifying deletion");
                Media media = createAndSave(photographer, url, "Profile Picture", MediaType.PROFILE_PICTURE);
                return media;
            } else {
                Media media = createAndSave(photographer, url, "Profile Picture", MediaType.PROFILE_PICTURE);
                photographer.setProfilePhotoUrl(url);
                photographerService.save(photographer);
                return media;
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to upload file");
        }
    }

    // private void deleteMediaEntity(Media media) {
    // try {
    // if (media != null && media.getMediaUrl() != null) {
    // deleteFile(media.getMediaUrl());
    // }
    // mediaRepository.delete(media); // Use delete instead of deleteById
    // mediaRepository.flush(); // Force immediate deletion
    // } catch (Exception e) {
    // System.out.println("Failed to delete media entity" + e.getMessage());
    // }
    // }

    @Transactional
    public Media getProfilePicturePhotographer(Long photographerId) {
        return mediaRepository.findProfilePictureByPhotographer(photographerId);
    }

}