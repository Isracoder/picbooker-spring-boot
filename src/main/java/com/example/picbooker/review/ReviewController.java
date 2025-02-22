package com.example.picbooker.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}")
    public ApiResponse<Review> findById(@PathVariable("id") long id) {

        return ApiResponse.<Review>builder()
                .content(reviewService.findByIdThrow(id))
                .status(HttpStatus.OK)
                .build();

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

}
