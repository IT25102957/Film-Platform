package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Payment {
    private int paymentId;
    private int rentalId;
    private int customerId;
    private String customerName;
    private double amount;
    private LocalDateTime paymentDate;
    private String status; // "PENDING", "COMPLETED", "REFUNDED", "FAILED"
    private String invoiceNumber;

    public abstract boolean processPayment();
    public abstract String getPaymentMethod();
    public abstract String getPaymentDetails();

    public String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + customerId;
    }
}

