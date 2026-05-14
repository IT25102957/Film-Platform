package com.movieapp.filmplatform.model;

public class PremiumPolicy extends com.movieapp.filmplatform.model.RentalPolicy {

    public PremiumPolicy() {
        super(2, "Premium", 0.8, 14);
    }

    public PremiumPolicy(int policyId, String policyName, double dailyRate, int maxRentalDays) {
        super(policyId, policyName, dailyRate, maxRentalDays);
    }

    @Override
    public double calculateLateFee(int daysLate, double basePrice) {
        // Premium policy: 1-day grace period, then $0.50 per day + 25% of daily rate
        if (daysLate <= 1) return 0;
        int chargeableDays = daysLate - 1;
        return (chargeableDays * 0.50) + (basePrice * 0.25 * chargeableDays);
    }

    @Override
    public int getGracePeriod() {
        return 1; // 1-day grace period
    }

    @Override
    public String getPolicyDescription() {
        return "Premium rental policy. 1-day grace period. Late fee: $0.50/day + 25% of daily rate.";
    }
}
