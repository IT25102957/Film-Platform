package com.movieapp.filmplatform.controller;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.service.MovieService;
import com.movieapp.filmplatform.service.RentalService;
import com.movieapp.filmplatform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private RentalService rentalService;

    // ==================== CUSTOMER PAGES ====================

    @GetMapping("/movies")
    public String browseMovies(Model model, HttpSession session,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "genre", required = false) String genre) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Movie> movies;
        if (search != null && !search.isEmpty()) {
            movies = movieService.searchMovies(search);
            model.addAttribute("searchQuery", search);
        } else if (genre != null && !genre.isEmpty()) {
            movies = movieService.getMoviesByGenre(genre);
            model.addAttribute("selectedGenre", genre);
        } else {
            movies = movieService.getAllMovies();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("user", user);
        return "customer/movie-catalog";
    }

    @GetMapping("/movies/{id}")
    public String movieDetails(@PathVariable int id, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Movie movie = movieService.getMovieById(id);
        if (movie == null) return "redirect:/movies";

        List<Rental> activeRentals = rentalService.getActiveRentalsByCustomer(user.getId());
        boolean hasActiveRental = activeRentals.stream()
                .anyMatch(r -> r.getMovieId() == id);

        List<Review> reviews = reviewService.getReviewsByMovie(id);
        RatingAggregator ratingAggregator = reviewService.getMovieRating(id);

        model.addAttribute("movie", movie);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("ratingAggregator", ratingAggregator);
        model.addAttribute("hasActiveRental", hasActiveRental);

        return "customer/movie-details";
    }

    // ==================== ADMIN MOVIE CRUD ====================

    @GetMapping("/admin/movies")
    public String adminMovies(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("loggedInUser", user);
        return "admin/movie/movie-list";
    }

    @GetMapping("/admin/movies/add")
    public String showAddMovieForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        model.addAttribute("movie", new NewRelease());
        model.addAttribute("isEdit", false);
        model.addAttribute("loggedInUser", user);
        return "admin/movie/movie-form";
    }

    @PostMapping("/admin/movies/add")
    public String addMovie(@RequestParam("title") String title,
                           @RequestParam("genre") String genre,
                           @RequestParam("year") int year,
                           @RequestParam("basePrice") double basePrice,
                           @RequestParam("type") String type,
                           @RequestParam(value = "posterUrl", required = false) String posterUrl,
                           @RequestParam(value = "description", required = false) String description,
                           @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
                           HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        Movie movie = createMovieByType(type);
        setMovieFields(movie, title, genre, year, basePrice, posterUrl, description, trailerUrl);

        movieService.addMovie(movie);
        return "redirect:/admin/movies?added=true";
    }

    @GetMapping("/admin/movies/edit/{id}")
    public String showEditMovieForm(@PathVariable int id, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        Movie movie = movieService.getMovieById(id);
        if (movie == null) return "redirect:/admin/movies?notfound=true";

        model.addAttribute("movie", movie);
        model.addAttribute("isEdit", true);
        model.addAttribute("loggedInUser", user);
        return "admin/movie/movie-form";
    }

    @PostMapping("/admin/movies/edit/{id}")
    public String updateMovie(@PathVariable int id,
                              @RequestParam("title") String title,
                              @RequestParam("genre") String genre,
                              @RequestParam("year") int year,
                              @RequestParam("basePrice") double basePrice,
                              @RequestParam("type") String type,
                              @RequestParam(value = "posterUrl", required = false) String posterUrl,
                              @RequestParam(value = "description", required = false) String description,
                              @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
                              HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        Movie existingMovie = movieService.getMovieById(id);
        if (existingMovie == null) return "redirect:/admin/movies?notfound=true";

        Movie updatedMovie = createMovieByType(type);
        updatedMovie.setId(id);
        setMovieFields(updatedMovie, title, genre, year, basePrice, posterUrl, description, trailerUrl);

        movieService.updateMovie(updatedMovie);
        return "redirect:/admin/movies?updated=true";
    }

    @PostMapping("/admin/movies/delete/{id}")
    public String deleteMoviePost(@PathVariable int id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";

        movieService.deleteMovie(id);
        return "redirect:/admin/movies?deleted=true";
    }

    // Keep this GET route also, in case an old delete link is still used somewhere.
    @GetMapping("/admin/movies/delete/{id}")
    public String deleteMovieGet(@PathVariable int id, HttpSession session) throws IOException {
        return deleteMoviePost(id, session);
    }

    private Movie createMovieByType(String type) {
        if ("Classic".equalsIgnoreCase(type)) {
            return new ClassicMovie();
        }
        return new NewRelease();
    }

    private void setMovieFields(Movie movie,
                                String title,
                                String genre,
                                int year,
                                double basePrice,
                                String posterUrl,
                                String description,
                                String trailerUrl) {
        movie.setTitle(title != null ? title.trim() : "");
        movie.setGenre(genre != null ? genre.trim() : "");
        movie.setYear(year);
        movie.setBasePrice(basePrice);
        movie.setPosterUrl(posterUrl != null ? posterUrl.trim() : "");
        movie.setDescription(description != null ? description.trim() : "");
        movie.setTrailerUrl(trailerUrl != null ? trailerUrl.trim() : "");
    }
}
