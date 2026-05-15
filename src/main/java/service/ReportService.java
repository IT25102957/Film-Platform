package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final UserService userService;
    private final MovieService movieService;
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    // Store generated reports
    private final List<Report> savedReports = new ArrayList<>();

    public ReportService(UserService userService, MovieService movieService,
                         RentalService rentalService, PaymentService paymentService,
                         ReviewService reviewService) {
        this.userService = userService;
        this.movieService = movieService;
        this.rentalService = rentalService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    // Get dashboard stats
    public DashboardStats getDashboardStats() throws IOException {
        DashboardStats stats = new DashboardStats();

        List<User> users = userService.getAllUsers();
        stats.setTotalUsers(users.size());
        stats.setActiveUsers((int) users.stream().filter(User::isActive).count());
        stats.setAdminUsers((int) users.stream().filter(u -> u.getRole().equals("admin")).count());

        List<Movie> movies = movieService.getAllMovies();
        stats.setTotalMovies(movies.size());
        stats.setTotalCopies(movies.size());
        stats.setAvailableCopies(movies.size());

        List<Rental> rentals = rentalService.getAllRentals();
        stats.setActiveRentals((int) rentals.stream().filter(r -> r.getReturnDate() == null).count());
        stats.setOverdueRentals(rentalService.getOverdueRentals().size());
        stats.setTodayRentals((int) rentals.stream()
                .filter(r -> r.getRentalDate().equals(LocalDate.now())).count());
        stats.setTodayReturns((int) rentals.stream()
                .filter(r -> r.getReturnDate() != null && r.getReturnDate().equals(LocalDate.now())).count());

        PaymentStats paymentStats = paymentService.getPaymentStats();
        stats.setTotalRevenue(paymentStats.getTotalRevenue());

        List<Review> reviews = reviewService.getAllReviews();
        stats.setTotalReviews(reviews.size());
        stats.setAverageRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0));

        // Recent activities
        List<String> activities = new ArrayList<>();
        rentals.stream()
                .sorted((r1, r2) -> r2.getRentalDate().compareTo(r1.getRentalDate()))
                .limit(5)
                .forEach(r -> activities.add(r.getCustomerName() + " rented " + r.getMovieTitle()));
        stats.setRecentActivities(activities);

        return stats;
    }

    // Filter rentals by date range
    private List<Rental> filterRentalsByDate(List<Rental> rentals, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return rentals;

        return rentals.stream()
                .filter(r -> {
                    boolean afterStart = startDate == null || !r.getRentalDate().isBefore(startDate);
                    boolean beforeEnd = endDate == null || !r.getRentalDate().isAfter(endDate);
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
    }

    // Filter payments by date range
    private List<Payment> filterPaymentsByDate(List<Payment> payments, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return payments;

        return payments.stream()
                .filter(p -> {
                    LocalDate paymentDate = p.getPaymentDate().toLocalDate();
                    boolean afterStart = startDate == null || !paymentDate.isBefore(startDate);
                    boolean beforeEnd = endDate == null || !paymentDate.isAfter(endDate);
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
    }

    // Generate Rental Report with date range
    public RentalReport generateRentalReport(LocalDate startDate, LocalDate endDate) throws IOException {
        RentalReport report = new RentalReport();
        report.setGeneratedDate(LocalDateTime.now());
        report.setReportId("RPT-" + System.currentTimeMillis());
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Rental> allRentals = rentalService.getAllRentals();
        List<Rental> filteredRentals = filterRentalsByDate(allRentals, startDate, endDate);

        report.setTotalRentals(filteredRentals.size());
        report.setActiveRentals((int) filteredRentals.stream().filter(r -> r.getReturnDate() == null).count());
        report.setOverdueRentals((int) filteredRentals.stream()
                .filter(r -> r.getReturnDate() == null && r.isOverdue()).count());
        report.setReturnedRentals((int) filteredRentals.stream().filter(r -> r.getReturnDate() != null).count());

        if (report.getTotalRentals() > 0) {
            report.setOccupancyRate(((double) report.getActiveRentals() / report.getTotalRentals()) * 100);
        }

        report.setSummary(report.generateContent());
        return report;
    }

    // Generate Revenue Report with date range
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) throws IOException {
        RevenueReport report = new RevenueReport();
        report.setGeneratedDate(LocalDateTime.now());
        report.setReportId("RPT-" + System.currentTimeMillis());
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Rental> allRentals = rentalService.getAllRentals();
        List<Rental> filteredRentals = filterRentalsByDate(allRentals, startDate, endDate);

        List<Payment> allPayments = paymentService.getAllPayments();
        List<Payment> filteredPayments = filterPaymentsByDate(allPayments, startDate, endDate);

        double totalRevenue = filteredPayments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum();
        double refundedAmount = filteredPayments.stream()
                .filter(p -> "REFUNDED".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum();

        report.setTotalRevenue(totalRevenue);
        report.setRefundedAmount(refundedAmount);
        report.setNetRevenue(totalRevenue - refundedAmount);

        double rentalRevenue = filteredRentals.stream()
                .filter(r -> r.getReturnDate() != null)
                .mapToDouble(Rental::getRentalPrice).sum();
        double lateFeeRevenue = filteredRentals.stream()
                .mapToDouble(Rental::getLateFee).sum();

        report.setRentalRevenue(rentalRevenue);
        report.setLateFeeRevenue(lateFeeRevenue);

        Map<String, Double> movieRevenue = new HashMap<>();
        for (Rental r : filteredRentals) {
            if (r.getReturnDate() != null) {
                movieRevenue.merge(r.getMovieTitle(), r.getRentalPrice() + r.getLateFee(), Double::sum);
            }
        }

        Map.Entry<String, Double> topMovie = movieRevenue.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
        if (topMovie != null) {
            report.setTopEarningMovie(topMovie.getKey());
            report.setTopMovieRevenue(topMovie.getValue());
        }

        report.setSummary(report.generateContent());
        return report;
    }

    // Generate Popularity Report with date range
    public PopularityReport generatePopularityReport(LocalDate startDate, LocalDate endDate) throws IOException {
        PopularityReport report = new PopularityReport();
        report.setGeneratedDate(LocalDateTime.now());
        report.setReportId("RPT-" + System.currentTimeMillis());
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Rental> allRentals = rentalService.getAllRentals();
        List<Rental> filteredRentals = filterRentalsByDate(allRentals, startDate, endDate);

        Map<String, Integer> rentalCounts = new HashMap<>();
        for (Rental r : filteredRentals) {
            rentalCounts.merge(r.getMovieTitle(), 1, Integer::sum);
        }

        Map.Entry<String, Integer> mostRented = rentalCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
        if (mostRented != null) {
            report.setMostRentedMovie(mostRented.getKey());
            report.setMostRentedCount(mostRented.getValue());
        }

        Map<String, Integer> genrePopularity = new HashMap<>();
        for (Rental r : filteredRentals) {
            try {
                Movie movie = movieService.getMovieById(r.getMovieId());
                if (movie != null) {
                    genrePopularity.merge(movie.getGenre(), 1, Integer::sum);
                }
            } catch (IOException e) {}
        }
        report.setGenrePopularity(genrePopularity);

        Map.Entry<String, Integer> topGenre = genrePopularity.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
        if (topGenre != null) {
            report.setMostPopularGenre(topGenre.getKey());
            report.setGenreRentalCount(topGenre.getValue());
        }

        List<String> topMovies = rentalCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());
        report.setTopMovies(topMovies);

        report.setSummary(report.generateContent());
        return report;
    }

    // Save report
    public void saveReport(Report report) {
        savedReports.add(report);
    }

    // Get all saved reports
    public List<Report> getSavedReports() {
        return savedReports;
    }

    // Delete a report
    public boolean deleteReport(String reportId) {
        return savedReports.removeIf(r -> r.getReportId().equals(reportId));
    }

    // Generate all reports
    public Map<String, Report> generateAllReports(LocalDate startDate, LocalDate endDate) throws IOException {
        Map<String, Report> reports = new LinkedHashMap<>();
        reports.put("rental", generateRentalReport(startDate, endDate));
        reports.put("revenue", generateRevenueReport(startDate, endDate));
        reports.put("popularity", generatePopularityReport(startDate, endDate));
        return reports;
    }

    // Export report as text
    public String exportReportAsText(Report report) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("ALPHA STUDIO FILMS - ").append(report.getReportName()).append("\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("Report ID: ").append(report.getReportId()).append("\n");
        sb.append("Generated: ").append(report.getFormattedDate()).append("\n");
        sb.append("Period: ").append(report.getDateRange()).append("\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append(report.generateContent()).append("\n");
        sb.append("=".repeat(50)).append("\n");
        return sb.toString();
    }
}