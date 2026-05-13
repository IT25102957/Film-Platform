package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Review {
    private int reviewId;
    private int movieId;
    private int customerId;
    private String customerName;
    private String movieTitle;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime reviewDate;
    private boolean containsSpoiler;
    private int helpfulVotes;
    private String status; // "ACTIVE", "HIDDEN", "DELETED"

    public abstract String getReviewType();
    public abstract double getWeight();
    public abstract String getBadgeClass();

    public String getShortComment() {
        if (comment == null) return "";
        return comment.length() > 100 ? comment.substring(0, 97) + "..." : comment;
    }

    public String getFormattedDate() {
        if (reviewDate == null) return "";
        return reviewDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public String getStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) stars.append("★");
            else stars.append("☆");
        }
        return stars.toString();
    }
}
