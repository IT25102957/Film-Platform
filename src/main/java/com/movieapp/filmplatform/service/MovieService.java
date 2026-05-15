package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final String FILE_PATH = "data/movies.txt";

    // CREATE: Add new movie
    public void addMovie(Movie movie) throws IOException {
        List<Movie> movies = getAllMovies();
        int newId = movies.isEmpty() ? 1 : movies.stream().mapToInt(Movie::getId).max().orElse(0) + 1;
        movie.setId(newId);

        String line = serializeMovie(movie);
        FileHandler.appendLine(FILE_PATH, line);
    }

    // READ: Get all movies
    public List<Movie> getAllMovies() throws IOException {
        List<Movie> movies = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Movie movie = deserializeMovie(line);
            if (movie != null) movies.add(movie);
        }
        return movies;
    }

    // READ: Get movie by ID
    public Movie getMovieById(int id) throws IOException {
        return getAllMovies().stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    // READ: Search movies
    public List<Movie> searchMovies(String keyword) throws IOException {
        String lowerKeyword = keyword.toLowerCase();
        return getAllMovies().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(lowerKeyword) ||
                        m.getGenre().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    // READ: Get movies by genre
    public List<Movie> getMoviesByGenre(String genre) throws IOException {
        return getAllMovies().stream()
                .filter(m -> m.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    // READ: Get available movies (always all for digital)
    public List<Movie> getAvailableMovies() throws IOException {
        return getAllMovies();
    }

    // UPDATE: Update movie details
    public void updateMovie(Movie updatedMovie) throws IOException {
        List<Movie> movies = getAllMovies();
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId() == updatedMovie.getId()) {
                movies.set(i, updatedMovie);
                break;
            }
        }
        saveAllMovies(movies);
    }

    // DELETE: Remove movie
    public void deleteMovie(int id) throws IOException {
        List<Movie> movies = getAllMovies();
        movies.removeIf(m -> m.getId() == id);
        saveAllMovies(movies);
    }

    // Get all unique genres
    public List<String> getAllGenres() throws IOException {
        return getAllMovies().stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Helper: Serialize movie to string
    private String serializeMovie(Movie movie) {
        return String.join("|",
                String.valueOf(movie.getId()),
                movie.getTitle(),
                movie.getGenre(),
                String.valueOf(movie.getYear()),
                String.valueOf(movie.getBasePrice()),
                movie.getType(),
                movie.getPosterUrl() != null ? movie.getPosterUrl() : "",
                movie.getDescription() != null ? movie.getDescription() : "",
                movie.getTrailerUrl() != null ? movie.getTrailerUrl() : ""
        );
    }

    // Helper: Deserialize string to movie
    private Movie deserializeMovie(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;

        int id = Integer.parseInt(parts[0]);
        String title = parts[1];
        String genre = parts[2];
        int year = Integer.parseInt(parts[3]);
        double basePrice = Double.parseDouble(parts[4]);
        String type = parts[5];
        String posterUrl = parts.length > 6 ? parts[6] : "";
        String description = parts.length > 7 ? parts[7] : "";
        String trailerUrl = parts.length > 8 ? parts[8] : "";

        if ("New Release".equals(type)) {
            return new NewRelease(id, title, genre, year, basePrice, posterUrl, description, trailerUrl);
        } else {
            return new ClassicMovie(id, title, genre, year, basePrice, posterUrl, description, trailerUrl);
        }
    }

    // Save all movies to file
    private void saveAllMovies(List<Movie> movies) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Movie movie : movies) {
            lines.add(serializeMovie(movie));
        }
        FileHandler.writeLines(FILE_PATH, lines);
    }
}