package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Movie {
    private int id;
    private String title;
    private String genre;
    private int year;
    private double basePrice;
    private String posterUrl;
    private String description;
    private String trailerUrl;

    public abstract double calculateRentalPrice(int days);
    public abstract String getType();

    // Always available for digital platform
    public boolean isAvailable() {
        return true;
    }

    public String getStockStatus() {
        return "Available";
    }

    public String getStockBadgeClass() {
        return "success";
    }
}