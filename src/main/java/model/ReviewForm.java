package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class ReviewForm {
    private int movieId;
    private int rating;
    private String comment;
    private boolean containsSpoiler;
}