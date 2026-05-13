package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class RentalForm {
    private int movieId;
    private int rentalDays;
    private String policyType;
}
