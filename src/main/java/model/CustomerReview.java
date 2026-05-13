package com.movieapp.filmplatform.model;

public class CustomerReview extends com.movieapp.filmplatform.model.Review {

    public CustomerReview() {
        super();
    }

    @Override
    public String getReviewType() {
        return "Customer";
    }

    @Override
    public double getWeight() {
        return 1.0; // Normal weight
    }

    @Override
    public String getBadgeClass() {
        return "secondary";
    }
}