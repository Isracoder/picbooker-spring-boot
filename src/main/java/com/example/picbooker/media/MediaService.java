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
import org.springframework.web.multipart.MultipartFile;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import jakarta.transaction.Transactional;

@Service
public class MediaService { // to think rename to media service

    @Autowired
    private MediaRepository mediaRepository;

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

    private void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }

    public Media findByIdThrow(Long mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Media not found"));

    }

    public long getCountForPhotographer(Long photographerId) {
        // to think of making sure profile pic not in count
        return mediaRepository.countByPhotographer_Id(photographerId);
    }

    public Media create(Photographer photographer, String url, String description, MediaType type) {
        return new Media(null, photographer, url, description, type);
    }

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
        Media media = findByIdThrow(id);
        if (media.getPhotographer().getId() != photographerId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your resource");
        }
        deleteFile(media.getMediaUrl());
        mediaRepository.deleteById(id);
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
    public Media uploadProfilePicture(Photographer photographer, MultipartFile file) {
        try {
            String url = uploadFile(file, MediaType.PROFILE_PICTURE);
            return createAndSave(photographer, url, "Profile Picture", MediaType.PROFILE_PICTURE);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to convert or upload file");
        }
    }

    @Transactional
    public Media getProfilePicturePhotographer(Long photographerId) {
        return mediaRepository.findProfilePictureByPhotographer(photographerId);
    }

}