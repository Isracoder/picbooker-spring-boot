package com.example.picbooker.media;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.picbooker.ApiException;
import com.example.picbooker.ApiResponse;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize; // 35MB

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize; // 35MG

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;

    }

    @PostMapping(value = "/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Media> uploadPhotoToGallery(
            @RequestPart("file") MultipartFile file,
            @RequestPart("description") String description,
            @RequestPart("mediaType") String mediaType) {
        Photographer photographer = UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

        if (isNull(file)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Null upload not permitted");
        }

        // Validate file size (e.g., 50MB limit)
        long maxFileSizeBytes = Integer.valueOf(maxFileSize.substring(0, 2)) * 1024 * 1024; // 305B in bytes
        if (file.getSize() > maxFileSizeBytes) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "File size exceeds the maximum allowed limit of " + maxFileSize);
        }

        // Validate media type (optional)
        if (!mediaType.equals("PHOTO") && !mediaType.equals("VIDEO")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid media type. Allowed types: PHOTO, VIDEO");
        }

        // Upload the media
        Media media = mediaService.uploadMediaForPhotographerGallery(photographer, file,
                com.example.picbooker.media.MediaType.valueOf(mediaType), description);

        return ApiResponse.<Media>builder()
                .content(media)
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/profile")
    public ApiResponse<Media> uploadProfilePicture(
            @RequestBody MultipartFile file) throws IOException {
        Photographer photographer = UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

        if (isNull(file))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Null photo upload not permitted");
        Media media = mediaService.uploadProfilePicture(photographer, file);

        return ApiResponse.<Media>builder()
                .content(media)
                .status(HttpStatus.OK)
                .build();

    }

    @GetMapping("/gallery")
    public ApiResponse<Page<Media>> getGallery(
            @RequestParam(name = "photographer") Long photographerId,
            @PageableDefault(size = 20, direction = Sort.Direction.ASC, sort = "id") Pageable page) {

        Page<Media> mediaPage = mediaService.getGalleryForPhotographer(page, photographerId);
        return ApiResponse.<Page<Media>>builder()
                .content(mediaPage)
                .status(HttpStatus.OK)
                .build();

    }

    @GetMapping("/profile")
    public ApiResponse<Media> getProfilePicture(
            @RequestParam(name = "photographer") Long photographerId) {

        Media media = mediaService.getProfilePicturePhotographer(photographerId);
        return ApiResponse.<Media>builder()
                .content(media)
                .status(HttpStatus.OK)
                .build();

    }

    @DeleteMapping("/{mediaId}")
    public ApiResponse<Map<String, String>> deleteMedia(@PathVariable("mediaId") Long mediaId) {
        mediaService.delete(mediaId,
                UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()).getId());
        return ApiResponse.<Map<String, String>>builder()
                .content(Map.of("status", "Success"))
                .status(HttpStatus.OK)
                .build();

    }
}
