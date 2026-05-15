package com.movieapp.filmplatform.model;

import lombok.Data;
import java.util.List;

@Data
public class RatingAggregator {
    private int movieId;
    private String movieTitle;
    private double averageRating;
    private double weightedAverageRating;
    private int totalReviews;
    private int criticReviews;
    private int customerReviews;
    private String ratingStars;

    public void calculateFromReviews(List<com.movieapp.filmplatform.model.Review> reviews) {
        this.totalReviews = reviews.size();
        this.criticReviews = (int) reviews.stream().filter(r -> r instanceof com.movieapp.filmplatform.model.CriticReview).count();
        this.customerReviews = totalReviews - criticReviews;

        if (totalReviews > 0) {
            double sum = reviews.stream().mapToInt(com.movieapp.filmplatform.model.Review::getRating).sum();
            this.averageRating = sum / totalReviews;

            double weightedSum = reviews.stream()
                    .mapToDouble(r -> r.getRating() * r.getWeight())
                    .sum();
            double totalWeight = reviews.stream().mapToDouble(com.movieapp.filmplatform.model.Review::getWeight).sum();
            this.weightedAverageRating = weightedSum / totalWeight;
        }

        this.ratingStars = generateStars(weightedAverageRating);
    }

    private String generateStars(double rating) {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) Math.floor(rating);
        boolean halfStar = (rating - fullStars) >= 0.5;

        for (int i = 0; i < fullStars; i++) stars.append("★");
        if (halfStar) stars.append("½");
        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
        for (int i = 0; i < emptyStars; i++) stars.append("☆");

        return stars.toString();
    }
}