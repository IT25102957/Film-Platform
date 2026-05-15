package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private String invoiceNumber;
    private int rentalId;
    private String customerName;
    private String movieTitle;
    private double rentalPrice;
    private double lateFee;
    private double totalAmount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String status;

    public String getFormattedDate() {
        if (paymentDate == null) return "";
        return paymentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public String getInvoiceHtml() {
        return "<div style='font-family: monospace;'>" +
                "================================<br>" +
                "       ALPHA STUDIO FILMS<br>" +
                "          INVOICE<br>" +
                "================================<br>" +
                "Invoice #: " + invoiceNumber + "<br>" +
                "Date: " + getFormattedDate() + "<br>" +
                "--------------------------------<br>" +
                "Customer: " + customerName + "<br>" +
                "Movie: " + movieTitle + "<br>" +
                "Rental Price: $" + String.format("%.2f", rentalPrice) + "<br>" +
                "Late Fee: $" + String.format("%.2f", lateFee) + "<br>" +
                "--------------------------------<br>" +
                "TOTAL: $" + String.format("%.2f", totalAmount) + "<br>" +
                "Payment Method: " + paymentMethod + "<br>" +
                "Status: " + status + "<br>" +
                "================================<br>" +
                "    Thank you for your rental!<br>" +
                "================================<br>" +
                "</div>";
    }
}

