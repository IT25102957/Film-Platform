package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class RentalStats {
    private int totalRentals;
    private int activeRentals;
    private int returnedRentals;
    private double totalSpent;
    private double totalLateFees;
    private String favoriteGenre;

    public int getOverdueRentals() {
        return totalRentals - activeRentals - returnedRentals;
    }
}
