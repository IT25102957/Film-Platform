package com.movieapp.filmplatform.model;

import java.util.List;
import java.util.Map;

public class PopularityReport extends com.movieapp.filmplatform.model.Report {
    private String mostRentedMovie;
    private int mostRentedCount;
    private String mostPopularGenre;
    private int genreRentalCount;
    private String topRatedMovie;
    private double topRating;
    private Map<String, Integer> genrePopularity;
    private List<String> topMovies;

    public PopularityReport() {
        setReportType("POPULARITY");
        setReportName("Popularity & Trends Report");
    }

    @Override
    public String generateContent() {
        return String.format(
                "Most Rented: %s (%d times)\nPopular Genre: %s (%d rentals)\nTop Rated: %s (%.1f stars)",
                mostRentedMovie, mostRentedCount, mostPopularGenre, genreRentalCount, topRatedMovie, topRating
        );
    }

    @Override
    public String getChartData() {
        if (genrePopularity == null) return "{}";
        StringBuilder json = new StringBuilder("{\"labels\":[");
        StringBuilder data = new StringBuilder("\"data\":[");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : genrePopularity.entrySet()) {
            if (!first) {
                json.append(",");
                data.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\"");
            data.append(entry.getValue());
            first = false;
        }
        json.append("],").append(data).append("]}");
        return json.toString();
    }

    @Override
    public String getIconClass() {
        return "fa-fire";
    }

    // Getters and Setters
    public String getMostRentedMovie() { return mostRentedMovie; }
    public void setMostRentedMovie(String mostRentedMovie) { this.mostRentedMovie = mostRentedMovie; }
    public int getMostRentedCount() { return mostRentedCount; }
    public void setMostRentedCount(int mostRentedCount) { this.mostRentedCount = mostRentedCount; }
    public String getMostPopularGenre() { return mostPopularGenre; }
    public void setMostPopularGenre(String mostPopularGenre) { this.mostPopularGenre = mostPopularGenre; }
    public int getGenreRentalCount() { return genreRentalCount; }
    public void setGenreRentalCount(int genreRentalCount) { this.genreRentalCount = genreRentalCount; }
    public String getTopRatedMovie() { return topRatedMovie; }
    public void setTopRatedMovie(String topRatedMovie) { this.topRatedMovie = topRatedMovie; }
    public double getTopRating() { return topRating; }
    public void setTopRating(double topRating) { this.topRating = topRating; }
    public Map<String, Integer> getGenrePopularity() { return genrePopularity; }
    public void setGenrePopularity(Map<String, Integer> genrePopularity) { this.genrePopularity = genrePopularity; }
    public List<String> getTopMovies() { return topMovies; }
    public void setTopMovies(List<String> topMovies) { this.topMovies = topMovies; }
}