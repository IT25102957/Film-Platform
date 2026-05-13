package com.movieapp.filmplatform.model;

public class RentalReport extends com.movieapp.filmplatform.model.Report {
    private int totalRentals;
    private int activeRentals;
    private int overdueRentals;
    private int returnedRentals;
    private double occupancyRate;

    public RentalReport() {
        setReportType("RENTAL");
        setReportName("Rental Activity Report");
    }

    @Override
    public String generateContent() {
        return String.format(
                "Total Rentals: %d\nActive: %d\nOverdue: %d\nReturned: %d\nOccupancy Rate: %.1f%%",
                totalRentals, activeRentals, overdueRentals, returnedRentals, occupancyRate
        );
    }

    @Override
    public String getChartData() {
        return String.format("{\"labels\":[\"Active\",\"Overdue\",\"Returned\"],\"data\":[%d,%d,%d]}",
                activeRentals, overdueRentals, returnedRentals);
    }

    @Override
    public String getIconClass() {
        return "fa-ticket-alt";
    }

    // Getters and Setters
    public int getTotalRentals() { return totalRentals; }
    public void setTotalRentals(int totalRentals) { this.totalRentals = totalRentals; }
    public int getActiveRentals() { return activeRentals; }
    public void setActiveRentals(int activeRentals) { this.activeRentals = activeRentals; }
    public int getOverdueRentals() { return overdueRentals; }
    public void setOverdueRentals(int overdueRentals) { this.overdueRentals = overdueRentals; }
    public int getReturnedRentals() { return returnedRentals; }
    public void setReturnedRentals(int returnedRentals) { this.returnedRentals = returnedRentals; }
    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
}