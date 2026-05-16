package com.movieapp.filmplatform.controller;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.service.MovieService;
import com.movieapp.filmplatform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MovieService movieService;

    // ==================== WRITE REVIEW ====================

    @GetMapping("/review/write/{movieId}")
    public String showWriteReview(@PathVariable int movieId, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) return "redirect:/movies";

        com.movieapp.filmplatform.model.ReviewForm form = new com.movieapp.filmplatform.model.ReviewForm();
        form.setMovieId(movieId);
        form.setRating(5);

        model.addAttribute("movie", movie);
        model.addAttribute("form", form);
        model.addAttribute("user", user);
        return "customer/review/write-review";
    }

    @PostMapping("/review/submit")
    public String submitReview(@ModelAttribute("form") com.movieapp.filmplatform.model.ReviewForm form,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        try {
            boolean isCritic = user.getRole().equals("admin");
            reviewService.submitReview(user.getId(), form, isCritic);
            redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "System error. Please try again.");
        }
        return "redirect:/movies/" + form.getMovieId();
    }

    // ==================== MY REVIEWS ====================

    @GetMapping("/my-reviews")
    public String myReviews(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("reviews", reviewService.getReviewsByCustomer(user.getId()));
        model.addAttribute("user", user);
        return "customer/review/my-reviews";
    }

    // ==================== EDIT REVIEW ====================

    @GetMapping("/review/edit/{reviewId}")
    public String showEditReview(@PathVariable int reviewId, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        com.movieapp.filmplatform.model.Review review = reviewService.getReviewById(reviewId);
        if (review == null) return "redirect:/my-reviews";
        if (review.getCustomerId() != user.getId()) return "redirect:/my-reviews";

        Movie movie = movieService.getMovieById(review.getMovieId());

        com.movieapp.filmplatform.model.ReviewForm form = new com.movieapp.filmplatform.model.ReviewForm();
        form.setMovieId(review.getMovieId());
        form.setRating(review.getRating());
        form.setComment(review.getComment());
        form.setContainsSpoiler(review.isContainsSpoiler());

        model.addAttribute("movie", movie);
        model.addAttribute("review", review);
        model.addAttribute("form", form);
        model.addAttribute("user", user);
        return "customer/review/edit-review";
    }

    @PostMapping("/review/update/{reviewId}")
    public String updateReview(@PathVariable int reviewId,
                               @ModelAttribute("form") com.movieapp.filmplatform.model.ReviewForm form,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        try {
            com.movieapp.filmplatform.model.Review review = reviewService.getReviewById(reviewId);
            if (review.getCustomerId() != user.getId()) {
                return "redirect:/my-reviews";
            }
            reviewService.updateReview(reviewId, form);
            redirectAttributes.addFlashAttribute("success", "Review updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-reviews";
    }

    // ==================== DELETE REVIEW ====================

    @GetMapping("/review/delete/{reviewId}")
    public String deleteReview(@PathVariable int reviewId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        try {
            com.movieapp.filmplatform.model.Review review = reviewService.getReviewById(reviewId);
            if (review.getCustomerId() != user.getId() && !user.getRole().equals("admin")) {
                return "redirect:/my-reviews";
            }
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("success", "Review deleted.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "System error.");
        }

        if (user.getRole().equals("admin")) {
            return "redirect:/admin/reviews";
        }
        return "redirect:/my-reviews";
    }

    // ==================== HELPFUL VOTE ====================

    @PostMapping("/review/helpful/{reviewId}")
    @ResponseBody
    public String markHelpful(@PathVariable int reviewId) {
        try {
            reviewService.markHelpful(reviewId);
            return "success";
        } catch (IOException e) {
            return "error";
        }
    }

    // ==================== ADMIN REVIEW MODERATION ====================

    @GetMapping("/admin/reviews")
    public String adminReviews(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        model.addAttribute("reviews", reviewService.getAllReviewsAdmin());
        model.addAttribute("loggedInUser", user);
        return "admin/review/review-list";
    }

    @PostMapping("/admin/reviews/toggle/{reviewId}")
    public String toggleReviewStatus(@PathVariable int reviewId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        try {
            reviewService.toggleReviewStatus(reviewId);
            redirectAttributes.addFlashAttribute("success", "Review status updated.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "System error.");
        }
        return "redirect:/admin/reviews";
    }
}