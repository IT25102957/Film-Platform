package com.movieapp.filmplatform.model;

import lombok.Data;
import java.util.List;

@Data
public class MovieInventory {
    private int totalMovies;
    private int totalCopies;
    private int availableCopies;
    private int rentedCopies;
    private List<Movie> lowStockMovies;
    private List<Movie> outOfStockMovies;

    public int getRentedCopies() {
        return totalCopies - availableCopies;
    }

    public double getOccupancyRate() {
        if (totalCopies == 0) return 0;
        return ((double) getRentedCopies() / totalCopies) * 100;
    }
}
