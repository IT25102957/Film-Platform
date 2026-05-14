package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class RentalPolicy {
    private int policyId;
    private String policyName;
    private double dailyRate;
    private int maxRentalDays;

    public abstract double calculateLateFee(int daysLate, double basePrice);
    public abstract int getGracePeriod();
    public abstract String getPolicyDescription();
}
