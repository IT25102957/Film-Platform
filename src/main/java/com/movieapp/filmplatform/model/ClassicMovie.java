package com.movieapp.filmplatform.model;

public class ClassicMovie extends Movie {

    public ClassicMovie() {}

    public ClassicMovie(int id, String title, String genre, int year,
                        double basePrice, String posterUrl, String description, String trailerUrl) {
        super(id, title, genre, year, basePrice, posterUrl, description, trailerUrl);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return getBasePrice() * days;
    }

    @Override
    public String getType() {
        return "Classic";
    }

    public String getBadgeClass() {
        return "secondary";
    }
}
