package com.movieapp.filmplatform.model;

import lombok.Data;
import java.util.List;

@Data
public class DashboardStats {
    // User stats
    private int totalUsers;
    private int activeUsers;
    private int adminUsers;
    private int newUsersToday;

    // Movie stats
    private int totalMovies;
    private int totalCopies;
    private int availableCopies;
    private int outOfStockMovies;
    private int lowStockMovies;

    // Rental stats
    private int activeRentals;
    private int overdueRentals;
    private int todayRentals;
    private int todayReturns;

    // Revenue stats
    private double todayRevenue;
    private double weekRevenue;
    private double monthRevenue;
    private double totalRevenue;

    // Review stats
    private int totalReviews;
    private double averageRating;
    private int pendingReviews;

    // Recent activities
    private List<String> recentActivities;

    // Chart data
    private String rentalChartData;
    private String revenueChartData;
    private String genreChartData;
}