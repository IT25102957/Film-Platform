package com.movieapp.filmplatform.model;

public class CriticReview extends com.movieapp.filmplatform.model.Review {

    public CriticReview() {
        super();
    }

    @Override
    public String getReviewType() {
        return "Critic";
    }

    @Override
    public double getWeight() {
        return 2.0; // Double weight for critics
    }

    @Override
    public String getBadgeClass() {
        return "primary";
    }
}
