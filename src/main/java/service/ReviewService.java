package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private static final String FILE_PATH = "data/reviews.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MovieService movieService;
    private final UserService userService;

    public ReviewService(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    // CREATE: Submit a review
    public com.movieapp.filmplatform.model.Review submitReview(int customerId, com.movieapp.filmplatform.model.ReviewForm form, boolean isCritic) throws IOException {
        Movie movie = movieService.getMovieById(form.getMovieId());
        if (movie == null) throw new IllegalArgumentException("Movie not found");

        User customer = userService.getUserById(customerId);
        if (customer == null) throw new IllegalArgumentException("User not found");

        // Check if already reviewed
        List<com.movieapp.filmplatform.model.Review> existingReviews = getReviewsByMovie(form.getMovieId());
        boolean alreadyReviewed = existingReviews.stream()
                .anyMatch(r -> r.getCustomerId() == customerId);
        if (alreadyReviewed) {
            throw new IllegalArgumentException("You have already reviewed this movie");
        }

        com.movieapp.filmplatform.model.Review review;
        if (isCritic || customer.getRole().equals("admin")) {
            review = new com.movieapp.filmplatform.model.CriticReview();
        } else {
            review = new com.movieapp.filmplatform.model.CustomerReview();
        }

        List<com.movieapp.filmplatform.model.Review> allReviews = getAllReviews();
        int newId = allReviews.isEmpty() ? 1 : allReviews.stream().mapToInt(com.movieapp.filmplatform.model.Review::getReviewId).max().orElse(0) + 1;

        review.setReviewId(newId);
        review.setMovieId(form.getMovieId());
        review.setCustomerId(customerId);
        review.setCustomerName(customer.getName());
        review.setMovieTitle(movie.getTitle());
        review.setRating(form.getRating());
        review.setComment(form.getComment());
        review.setReviewDate(LocalDateTime.now());
        review.setContainsSpoiler(form.isContainsSpoiler());
        review.setHelpfulVotes(0);
        review.setStatus("ACTIVE");

        String line = serializeReview(review);
        FileHandler.appendLine(FILE_PATH, line);

        return review;
    }

    // READ: Get all reviews
    public List<com.movieapp.filmplatform.model.Review> getAllReviews() throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            com.movieapp.filmplatform.model.Review review = deserializeReview(line);
            if (review != null && !"DELETED".equals(review.getStatus())) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    // READ: Get reviews by movie
    public List<com.movieapp.filmplatform.model.Review> getReviewsByMovie(int movieId) throws IOException {
        return getAllReviews().stream()
                .filter(r -> r.getMovieId() == movieId && "ACTIVE".equals(r.getStatus()))
                .sorted((r1, r2) -> r2.getReviewDate().compareTo(r1.getReviewDate()))
                .collect(Collectors.toList());
    }

    // READ: Get reviews by customer
    public List<com.movieapp.filmplatform.model.Review> getReviewsByCustomer(int customerId) throws IOException {
        return getAllReviews().stream()
                .filter(r -> r.getCustomerId() == customerId)
                .sorted((r1, r2) -> r2.getReviewDate().compareTo(r1.getReviewDate()))
                .collect(Collectors.toList());
    }

    // READ: Get review by ID
    public com.movieapp.filmplatform.model.Review getReviewById(int reviewId) throws IOException {
        return getAllReviews().stream()
                .filter(r -> r.getReviewId() == reviewId)
                .findFirst()
                .orElse(null);
    }

    // READ: Get rating aggregator for movie
    public com.movieapp.filmplatform.model.RatingAggregator getMovieRating(int movieId) throws IOException {
        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) return null;

        List<com.movieapp.filmplatform.model.Review> reviews = getReviewsByMovie(movieId);
        com.movieapp.filmplatform.model.RatingAggregator aggregator = new com.movieapp.filmplatform.model.RatingAggregator();
        aggregator.setMovieId(movieId);
        aggregator.setMovieTitle(movie.getTitle());
        aggregator.calculateFromReviews(reviews);

        return aggregator;
    }

    // UPDATE: Edit review
    public com.movieapp.filmplatform.model.Review updateReview(int reviewId, com.movieapp.filmplatform.model.ReviewForm form) throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = getAllReviews();
        com.movieapp.filmplatform.model.Review review = findReviewById(reviews, reviewId);

        if (review == null) throw new IllegalArgumentException("Review not found");

        review.setRating(form.getRating());
        review.setComment(form.getComment());
        review.setContainsSpoiler(form.isContainsSpoiler());
        review.setReviewDate(LocalDateTime.now()); // Update timestamp

        saveAllReviews(reviews);
        return review;
    }

    // UPDATE: Mark helpful
    public void markHelpful(int reviewId) throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = getAllReviews();
        com.movieapp.filmplatform.model.Review review = findReviewById(reviews, reviewId);

        if (review != null) {
            review.setHelpfulVotes(review.getHelpfulVotes() + 1);
            saveAllReviews(reviews);
        }
    }

    // DELETE: Delete review (soft delete)
    public void deleteReview(int reviewId) throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = getAllReviews();
        com.movieapp.filmplatform.model.Review review = findReviewById(reviews, reviewId);

        if (review != null) {
            review.setStatus("DELETED");
            saveAllReviews(reviews);
        }
    }

    // ADMIN: Hide/Unhide review
    public void toggleReviewStatus(int reviewId) throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = getAllReviews();
        com.movieapp.filmplatform.model.Review review = findReviewById(reviews, reviewId);

        if (review != null) {
            review.setStatus("ACTIVE".equals(review.getStatus()) ? "HIDDEN" : "ACTIVE");
            saveAllReviews(reviews);
        }
    }

    // ADMIN: Get all reviews (including hidden)
    public List<com.movieapp.filmplatform.model.Review> getAllReviewsAdmin() throws IOException {
        List<com.movieapp.filmplatform.model.Review> reviews = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            com.movieapp.filmplatform.model.Review review = deserializeReview(line);
            if (review != null) reviews.add(review);
        }
        return reviews.stream()
                .sorted((r1, r2) -> r2.getReviewDate().compareTo(r1.getReviewDate()))
                .collect(Collectors.toList());
    }

    // Get top rated movies
    public List<com.movieapp.filmplatform.model.RatingAggregator> getTopRatedMovies(int limit) throws IOException {
        List<com.movieapp.filmplatform.model.RatingAggregator> ratings = new ArrayList<>();
        List<Movie> movies = movieService.getAllMovies();

        for (Movie movie : movies) {
            com.movieapp.filmplatform.model.RatingAggregator agg = getMovieRating(movie.getId());
            if (agg.getTotalReviews() > 0) {
                ratings.add(agg);
            }
        }

        return ratings.stream()
                .sorted((r1, r2) -> Double.compare(r2.getWeightedAverageRating(), r1.getWeightedAverageRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Helper: Find review by ID
    private com.movieapp.filmplatform.model.Review findReviewById(List<com.movieapp.filmplatform.model.Review> reviews, int reviewId) {
        return reviews.stream()
                .filter(r -> r.getReviewId() == reviewId)
                .findFirst()
                .orElse(null);
    }

    // Helper: Serialize review
    private String serializeReview(com.movieapp.filmplatform.model.Review review) {
        String type = review instanceof com.movieapp.filmplatform.model.CriticReview ? "CRITIC" : "CUSTOMER";
        return String.join("|",
                String.valueOf(review.getReviewId()),
                String.valueOf(review.getMovieId()),
                String.valueOf(review.getCustomerId()),
                review.getCustomerName(),
                review.getMovieTitle(),
                String.valueOf(review.getRating()),
                review.getComment() != null ? review.getComment().replace("|", " ") : "",
                review.getReviewDate().format(DATE_FORMATTER),
                String.valueOf(review.isContainsSpoiler()),
                String.valueOf(review.getHelpfulVotes()),
                review.getStatus(),
                type
        );
    }

    // Helper: Deserialize review
    private com.movieapp.filmplatform.model.Review deserializeReview(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 12) return null;

        try {
            String type = parts[11];
            com.movieapp.filmplatform.model.Review review = "CRITIC".equals(type) ? new com.movieapp.filmplatform.model.CriticReview() : new com.movieapp.filmplatform.model.CustomerReview();

            review.setReviewId(Integer.parseInt(parts[0]));
            review.setMovieId(Integer.parseInt(parts[1]));
            review.setCustomerId(Integer.parseInt(parts[2]));
            review.setCustomerName(parts[3]);
            review.setMovieTitle(parts[4]);
            review.setRating(Integer.parseInt(parts[5]));
            review.setComment(parts[6]);
            review.setReviewDate(LocalDateTime.parse(parts[7], DATE_FORMATTER));
            review.setContainsSpoiler(Boolean.parseBoolean(parts[8]));
            review.setHelpfulVotes(Integer.parseInt(parts[9]));
            review.setStatus(parts[10]);

            return review;
        } catch (Exception e) {
            return null;
        }
    }

    // Save all reviews
    private void saveAllReviews(List<com.movieapp.filmplatform.model.Review> reviews) throws IOException {
        List<String> lines = new ArrayList<>();
        for (com.movieapp.filmplatform.model.Review review : reviews) {
            lines.add(serializeReview(review));
        }
        FileHandler.writeLines(FILE_PATH, lines);
    }
}