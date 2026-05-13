package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    private int rentalId;
    private int customerId;
    private int movieId;
    private String customerName;
    private String movieTitle;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int rentalDays;
    private double rentalPrice;
    private double lateFee;
    private String policyType; // "Standard" or "Premium"
    private String status; // "ACTIVE", "RETURNED", "OVERDUE", "CANCELLED"

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (returnDate != null || !isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double calculateCurrentLateFee(double basePrice) {
        long daysLate = getDaysOverdue();
        if (daysLate <= 0) return 0;

        if ("Premium".equals(policyType)) {
            return new PremiumPolicy().calculateLateFee((int) daysLate, basePrice);
        } else {
            return new StandardPolicy().calculateLateFee((int) daysLate, basePrice);
        }
    }

    public String getStatusBadgeClass() {
        switch (status) {
            case "ACTIVE": return "success";
            case "OVERDUE": return "danger";
            case "RETURNED": return "secondary";
            case "CANCELLED": return "warning";
            default: return "info";
        }
    }

    public String getPolicyBadgeClass() {
        return "Premium".equals(policyType) ? "primary" : "secondary";
    }
}