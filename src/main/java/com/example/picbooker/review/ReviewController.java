package com.example.picbooker.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService service;

    // @GetMapping("/")
    // public ApiResponse<?> findAllReviews(
    // @PageableDefault(size = 10, direction = Sort.Direction.ASC, sort = "id")
    // Pageable page) {
    // try {
    // Page<Review> reviewsResPage = service.findAll(page);
    // return ApiResponse.<List<Review>>builder()
    // .content(reviewsResPage.getContent())
    // .status(HttpStatus.OK)
    // .build();
    // } catch (Exception e) {
    // return ApiResponse.<String>builder()
    // .content("Something went wrong :(")
    // .status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .build();
    // }
    // }

    @GetMapping("/{id}")
    public ApiResponse<?> findById(@PathVariable("id") long id) {
        try {
            return ApiResponse.<String>builder()
                    .content("Not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}") // to do secure only for admin
    public ApiResponse<?> deleteById(@PathVariable("id") long id) {
        try {

            // to do implement
            return ApiResponse.<String>builder()
                    .content("Successful deletion")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content("Something went wrong :(")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/")
    public ApiResponse<String> createReview(@RequestBody ReviewDTO Review) {
        // Review reviewRes = service.save(Review);
        return ApiResponse.<String>builder()
                .content("not implemented")
                .status(HttpStatus.OK)
                .build();

    }

    @GetMapping("/{photographerId}")
    public ApiResponse<?> getReviewByPhotographer(@PathVariable("photographerId") long id) {
        try {
            // Review reviewRes = service.update(Review, id);
            return ApiResponse.<String>builder()
                    .content("Not implemented")
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .content(e.getLocalizedMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

}
