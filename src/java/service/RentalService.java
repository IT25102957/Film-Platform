package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalService {
    private static final String FILE_PATH = "data/rentals.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MovieService movieService;
    private final UserService userService;

    public RentalService(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    private com.movieapp.filmplatform.model.Rental findRentalById(List<com.movieapp.filmplatform.model.Rental> rentals, int rentalId) {
        return rentals.stream()
                .filter(r -> r.getRentalId() == rentalId)
                .findFirst()
                .orElse(null);
    }

    // CREATE: Rent a movie (no inventory update for digital platform)
    public com.movieapp.filmplatform.model.Rental rentMovie(int customerId, int movieId, int rentalDays, String policyType) throws IOException {
        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) throw new IllegalArgumentException("Movie not found");

        com.movieapp.filmplatform.model.RentalPolicy policy = getPolicyByType(policyType);
        if (rentalDays > policy.getMaxRentalDays()) {
            throw new IllegalArgumentException("Maximum rental days for " + policyType + " is " + policy.getMaxRentalDays());
        }

        User customer = userService.getUserById(customerId);
        if (customer == null) throw new IllegalArgumentException("Customer not found");

        List<com.movieapp.filmplatform.model.Rental> rentals = getAllRentals();
        int newId = rentals.isEmpty() ? 1 : rentals.stream().mapToInt(com.movieapp.filmplatform.model.Rental::getRentalId).max().orElse(0) + 1;

        com.movieapp.filmplatform.model.Rental rental = new com.movieapp.filmplatform.model.Rental();
        rental.setRentalId(newId);
        rental.setCustomerId(customerId);
        rental.setMovieId(movieId);
        rental.setCustomerName(customer.getName());
        rental.setMovieTitle(movie.getTitle());
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(rentalDays));
        rental.setRentalDays(rentalDays);
        rental.setPolicyType(policyType);
        rental.setStatus("ACTIVE");

        double basePrice = movie.calculateRentalPrice(rentalDays);
        rental.setRentalPrice(basePrice);
        rental.setLateFee(0);

        String line = serializeRental(rental);
        FileHandler.appendLine(FILE_PATH, line);

        return rental;
    }

    public List<com.movieapp.filmplatform.model.Rental> getAllRentals() throws IOException {
        List<com.movieapp.filmplatform.model.Rental> rentals = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            com.movieapp.filmplatform.model.Rental rental = deserializeRental(line);
            if (rental != null) {
                if (rental.getReturnDate() == null && rental.isOverdue()) {
                    rental.setStatus("OVERDUE");
                }
                rentals.add(rental);
            }
        }
        return rentals;
    }

    public com.movieapp.filmplatform.model.Rental getRentalById(int rentalId) throws IOException {
        return findRentalById(getAllRentals(), rentalId);
    }

    public List<com.movieapp.filmplatform.model.Rental> getRentalsByCustomer(int customerId) throws IOException {
        return getAllRentals().stream()
                .filter(r -> r.getCustomerId() == customerId)
                .sorted((r1, r2) -> r2.getRentalDate().compareTo(r1.getRentalDate()))
                .collect(Collectors.toList());
    }

    public List<com.movieapp.filmplatform.model.Rental> getActiveRentalsByCustomer(int customerId) throws IOException {
        return getRentalsByCustomer(customerId).stream()
                .filter(r -> r.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    public List<com.movieapp.filmplatform.model.Rental> getOverdueRentals() throws IOException {
        return getAllRentals().stream()
                .filter(r -> r.getReturnDate() == null && r.isOverdue())
                .collect(Collectors.toList());
    }

    // UPDATE: Return a movie (no inventory update for digital)
    public com.movieapp.filmplatform.model.Rental returnMovie(int rentalId) throws IOException {
        List<com.movieapp.filmplatform.model.Rental> rentals = getAllRentals();
        com.movieapp.filmplatform.model.Rental rental = findRentalById(rentals, rentalId);

        if (rental == null) throw new IllegalArgumentException("Rental not found");
        if (rental.getReturnDate() != null) throw new IllegalArgumentException("Already returned");

        rental.setReturnDate(LocalDate.now());
        rental.setStatus("RETURNED");

        if (rental.getReturnDate().isAfter(rental.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(rental.getDueDate(), rental.getReturnDate());
            Movie movie = movieService.getMovieById(rental.getMovieId());
            double basePrice = movie.calculateRentalPrice(1);
            rental.setLateFee(calculateLateFee(daysLate, basePrice, rental.getPolicyType()));
        }

        saveAllRentals(rentals);
        return rental;
    }

    public com.movieapp.filmplatform.model.Rental extendRental(int rentalId, int additionalDays) throws IOException {
        List<com.movieapp.filmplatform.model.Rental> rentals = getAllRentals();
        com.movieapp.filmplatform.model.Rental rental = findRentalById(rentals, rentalId);

        if (rental == null) throw new IllegalArgumentException("Rental not found");
        if (rental.getReturnDate() != null) throw new IllegalArgumentException("Already returned");

        com.movieapp.filmplatform.model.RentalPolicy policy = getPolicyByType(rental.getPolicyType());
        int newTotalDays = rental.getRentalDays() + additionalDays;
        if (newTotalDays > policy.getMaxRentalDays()) {
            throw new IllegalArgumentException("Maximum rental days exceeded");
        }

        Movie movie = movieService.getMovieById(rental.getMovieId());
        double additionalCost = movie.calculateRentalPrice(additionalDays);

        rental.setRentalDays(newTotalDays);
        rental.setDueDate(rental.getDueDate().plusDays(additionalDays));
        rental.setRentalPrice(rental.getRentalPrice() + additionalCost);

        saveAllRentals(rentals);
        return rental;
    }

    // DELETE: Cancel rental (no inventory update for digital)
    public void cancelRental(int rentalId) throws IOException {
        List<com.movieapp.filmplatform.model.Rental> rentals = getAllRentals();
        com.movieapp.filmplatform.model.Rental rental = findRentalById(rentals, rentalId);

        if (rental == null) throw new IllegalArgumentException("Rental not found");
        if (rental.getReturnDate() != null) throw new IllegalArgumentException("Already returned");

        rental.setStatus("CANCELLED");
        rental.setReturnDate(LocalDate.now());
        saveAllRentals(rentals);
    }

    private com.movieapp.filmplatform.model.RentalPolicy getPolicyByType(String type) {
        return "Premium".equals(type) ? new com.movieapp.filmplatform.model.PremiumPolicy() : new com.movieapp.filmplatform.model.StandardPolicy();
    }

    private double calculateLateFee(long daysLate, double basePrice, String policyType) {
        if (daysLate <= 0) return 0;
        com.movieapp.filmplatform.model.RentalPolicy policy = getPolicyByType(policyType);
        return policy.calculateLateFee((int) daysLate, basePrice);
    }

    public com.movieapp.filmplatform.model.RentalStats getCustomerStats(int customerId) throws IOException {
        List<com.movieapp.filmplatform.model.Rental> customerRentals = getRentalsByCustomer(customerId);

        com.movieapp.filmplatform.model.RentalStats stats = new com.movieapp.filmplatform.model.RentalStats();
        stats.setTotalRentals(customerRentals.size());
        stats.setActiveRentals((int) customerRentals.stream().filter(r -> r.getReturnDate() == null).count());
        stats.setReturnedRentals((int) customerRentals.stream().filter(r -> r.getReturnDate() != null && !"CANCELLED".equals(r.getStatus())).count());
        stats.setTotalSpent(customerRentals.stream().mapToDouble(com.movieapp.filmplatform.model.Rental::getRentalPrice).sum());
        stats.setTotalLateFees(customerRentals.stream().mapToDouble(com.movieapp.filmplatform.model.Rental::getLateFee).sum());

        Map<String, Integer> genreCount = new HashMap<>();
        for (com.movieapp.filmplatform.model.Rental rental : customerRentals) {
            try {
                Movie movie = movieService.getMovieById(rental.getMovieId());
                if (movie != null) genreCount.merge(movie.getGenre(), 1, Integer::sum);
            } catch (IOException e) {}
        }
        stats.setFavoriteGenre(genreCount.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("None"));

        return stats;
    }

    public List<Recommendation> getRecommendations(int customerId) throws IOException {
        List<Recommendation> recommendations = new ArrayList<>();
        com.movieapp.filmplatform.model.RentalStats stats = getCustomerStats(customerId);

        if (stats.getFavoriteGenre() != null && !"None".equals(stats.getFavoriteGenre())) {
            List<Movie> genreMovies = movieService.getMoviesByGenre(stats.getFavoriteGenre());
            List<com.movieapp.filmplatform.model.Rental> customerRentals = getRentalsByCustomer(customerId);
            Set<Integer> rentedMovieIds = customerRentals.stream().map(com.movieapp.filmplatform.model.Rental::getMovieId).collect(Collectors.toSet());

            for (Movie movie : genreMovies) {
                if (!rentedMovieIds.contains(movie.getId())) {
                    recommendations.add(new Recommendation(movie, "Because you like " + stats.getFavoriteGenre() + " movies", 0.8));
                }
            }
        }

        if (recommendations.size() < 5) {
            List<Movie> allMovies = movieService.getAvailableMovies();
            List<com.movieapp.filmplatform.model.Rental> customerRentals = getRentalsByCustomer(customerId);
            Set<Integer> rentedMovieIds = customerRentals.stream().map(com.movieapp.filmplatform.model.Rental::getMovieId).collect(Collectors.toSet());

            for (Movie movie : allMovies) {
                if (!rentedMovieIds.contains(movie.getId()) &&
                        recommendations.stream().noneMatch(r -> r.getMovie().getId() == movie.getId())) {
                    recommendations.add(new Recommendation(movie, "Popular choice", 0.5));
                    if (recommendations.size() >= 5) break;
                }
            }
        }

        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    private String serializeRental(com.movieapp.filmplatform.model.Rental rental) {
        return String.join("|",
                String.valueOf(rental.getRentalId()),
                String.valueOf(rental.getCustomerId()),
                String.valueOf(rental.getMovieId()),
                rental.getCustomerName(),
                rental.getMovieTitle(),
                rental.getRentalDate().format(DATE_FORMATTER),
                rental.getDueDate().format(DATE_FORMATTER),
                rental.getReturnDate() != null ? rental.getReturnDate().format(DATE_FORMATTER) : "",
                String.valueOf(rental.getRentalDays()),
                String.valueOf(rental.getRentalPrice()),
                String.valueOf(rental.getLateFee()),
                rental.getPolicyType(),
                rental.getStatus()
        );
    }

    private com.movieapp.filmplatform.model.Rental deserializeRental(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 13) return null;

        try {
            com.movieapp.filmplatform.model.Rental rental = new com.movieapp.filmplatform.model.Rental();
            rental.setRentalId(Integer.parseInt(parts[0]));
            rental.setCustomerId(Integer.parseInt(parts[1]));
            rental.setMovieId(Integer.parseInt(parts[2]));
            rental.setCustomerName(parts[3]);
            rental.setMovieTitle(parts[4]);
            rental.setRentalDate(LocalDate.parse(parts[5], DATE_FORMATTER));
            rental.setDueDate(LocalDate.parse(parts[6], DATE_FORMATTER));
            rental.setReturnDate(parts[7].isEmpty() ? null : LocalDate.parse(parts[7], DATE_FORMATTER));
            rental.setRentalDays(Integer.parseInt(parts[8]));
            rental.setRentalPrice(Double.parseDouble(parts[9]));
            rental.setLateFee(Double.parseDouble(parts[10]));
            rental.setPolicyType(parts[11]);
            rental.setStatus(parts[12]);
            return rental;
        } catch (Exception e) {
            return null;
        }
    }

    private void saveAllRentals(List<com.movieapp.filmplatform.model.Rental> rentals) throws IOException {
        List<String> lines = new ArrayList<>();
        for (com.movieapp.filmplatform.model.Rental rental : rentals) {
            lines.add(serializeRental(rental));
        }
        FileHandler.writeLines(FILE_PATH, lines);
    }
}