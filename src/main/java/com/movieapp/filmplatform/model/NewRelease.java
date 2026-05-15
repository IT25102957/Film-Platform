package com.movieapp.filmplatform.model;

public class NewRelease extends Movie {

    public NewRelease() {}

    public NewRelease(int id, String title, String genre, int year,
                      double basePrice, String posterUrl, String description, String trailerUrl) {
        super(id, title, genre, year, basePrice, posterUrl, description, trailerUrl);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return getBasePrice() * days;
    }

    @Override
    public String getType() {
        return "New Release";
    }

    public String getBadgeClass() {
        return "primary";
    }
}
