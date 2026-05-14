package com.movieapp.filmplatform.model;

public class StandardPolicy extends com.movieapp.filmplatform.model.RentalPolicy {

    public StandardPolicy() {
        super(1, "Standard", 1.0, 7);
    }

    public StandardPolicy(int policyId, String policyName, double dailyRate, int maxRentalDays) {
        super(policyId, policyName, dailyRate, maxRentalDays);
    }

    @Override
    public double calculateLateFee(int daysLate, double basePrice) {
        // Standard policy: $1.00 per day late + 50% of daily rental price
        return (daysLate * 1.0) + (basePrice * 0.5 * daysLate);
    }

    @Override
    public int getGracePeriod() {
        return 0; // No grace period for standard
    }

    @Override
    public String getPolicyDescription() {
        return "Standard rental policy. Late fee: $1/day + 50% of daily rate.";
    }
}
