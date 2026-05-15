package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class PaymentStats {
    private int totalPayments;
    private double totalRevenue;
    private double refundedAmount;
    private int pendingPayments;

    public double getNetRevenue() {
        return totalRevenue - refundedAmount;
    }
}
