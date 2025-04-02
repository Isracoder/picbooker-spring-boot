package com.example.picbooker.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

}
