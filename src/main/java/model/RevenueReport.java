package com.movieapp.filmplatform.model;

public class RevenueReport extends com.movieapp.filmplatform.model.Report {
    private double totalRevenue;
    private double rentalRevenue;
    private double lateFeeRevenue;
    private double refundedAmount;
    private double netRevenue;
    private String topEarningMovie;
    private double topMovieRevenue;

    public RevenueReport() {
        setReportType("REVENUE");
        setReportName("Revenue Analysis Report");
    }

    @Override
    public String generateContent() {
        return String.format(
                "Total Revenue: $%.2f\nRental Revenue: $%.2f\nLate Fees: $%.2f\nRefunded: $%.2f\nNet Revenue: $%.2f\nTop Movie: %s ($%.2f)",
                totalRevenue, rentalRevenue, lateFeeRevenue, refundedAmount, netRevenue, topEarningMovie, topMovieRevenue
        );
    }

    @Override
    public String getChartData() {
        return String.format("{\"labels\":[\"Rentals\",\"Late Fees\",\"Refunds\"],\"data\":[%.2f,%.2f,%.2f]}",
                rentalRevenue, lateFeeRevenue, refundedAmount);
    }

    @Override
    public String getIconClass() {
        return "fa-dollar-sign";
    }

    // Getters and Setters
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getRentalRevenue() { return rentalRevenue; }
    public void setRentalRevenue(double rentalRevenue) { this.rentalRevenue = rentalRevenue; }
    public double getLateFeeRevenue() { return lateFeeRevenue; }
    public void setLateFeeRevenue(double lateFeeRevenue) { this.lateFeeRevenue = lateFeeRevenue; }
    public double getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(double refundedAmount) { this.refundedAmount = refundedAmount; }
    public double getNetRevenue() { return netRevenue; }
    public void setNetRevenue(double netRevenue) { this.netRevenue = netRevenue; }
    public String getTopEarningMovie() { return topEarningMovie; }
    public void setTopEarningMovie(String topEarningMovie) { this.topEarningMovie = topEarningMovie; }
    public double getTopMovieRevenue() { return topMovieRevenue; }
    public void setTopMovieRevenue(double topMovieRevenue) { this.topMovieRevenue = topMovieRevenue; }
}