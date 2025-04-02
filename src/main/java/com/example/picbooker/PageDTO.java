package com.example.picbooker;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private List<T> content;
    private int totalPages;

    private long totalElements;
    private int currentPage;

}