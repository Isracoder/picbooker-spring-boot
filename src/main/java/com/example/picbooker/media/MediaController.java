package com.example.picbooker.media;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;

    public MediaController(MediaService mediaService, MediaRepository mediaRepository) {
        this.mediaService = mediaService;
        this.mediaRepository = mediaRepository;
    }

    @PostMapping("/gallery")
    public ApiResponse<Media> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType mediaType,
            @RequestParam("description") String description) throws IOException {
        Photographer photographer = UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

        Media media = mediaService.uploadMediaForPhotographerGallery(photographer, file, mediaType, description);

        return ApiResponse.<Media>builder()
                .content(media)
                .status(HttpStatus.OK)
                .build();

    }

    @PostMapping("/profile")
    public ApiResponse<Media> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) throws IOException {
        Photographer photographer = UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow());

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

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMedia(@PathVariable Long id) {
        mediaService.delete(id, UserService.getPhotographerFromUserThrow(UserService.getLoggedInUserThrow()).getId());
        return ApiResponse.<String>builder()
                .content("Successful deletion")
                .status(HttpStatus.OK)
                .build();

    }
}
