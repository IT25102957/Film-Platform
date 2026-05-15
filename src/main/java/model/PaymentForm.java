package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class PaymentForm {
    private int rentalId;
    private String paymentMethod; // "CARD" or "CASH"
    private String cardNumber;
    private String cardType;
    private Double cashAmount;
}
