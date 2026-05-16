package com.movieapp.filmplatform.controller;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.service.PaymentService;
import com.movieapp.filmplatform.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RentalService rentalService;

    // CUSTOMER: Show payment page
    @GetMapping("/pay/{rentalId}")
    public String showPaymentPage(@PathVariable int rentalId, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Rental rental = rentalService.getRentalById(rentalId);
        if (rental == null) return "redirect:/my-rentals";
        if (rental.getCustomerId() != user.getId()) return "redirect:/my-rentals";

        // Check if already paid. One rental has one invoice, so redirect to that invoice.
        com.movieapp.filmplatform.model.Payment existingPayment = paymentService.getPaymentByRental(rentalId);
        if (existingPayment != null && "COMPLETED".equals(existingPayment.getStatus())) {
            return "redirect:/invoice/" + existingPayment.getPaymentId();
        }

        com.movieapp.filmplatform.model.PaymentForm form = new com.movieapp.filmplatform.model.PaymentForm();
        form.setRentalId(rentalId);
        form.setPaymentMethod("CARD");

        model.addAttribute("rental", rental);
        model.addAttribute("form", form);
        model.addAttribute("user", user);
        return "customer/payment/payment-form";
    }

    // CUSTOMER: Process payment
    @PostMapping("/pay/{rentalId}")
    public String processPayment(@PathVariable int rentalId,
                                 @ModelAttribute("form") com.movieapp.filmplatform.model.PaymentForm form,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        try {
            com.movieapp.filmplatform.model.Payment payment = paymentService.processPayment(rentalId, form);
            if ("COMPLETED".equals(payment.getStatus())) {
                redirectAttributes.addFlashAttribute("success", "Payment successful!");
                return "redirect:/invoice/" + payment.getPaymentId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment failed. Please try again.");
                return "redirect:/pay/" + rentalId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pay/" + rentalId;
        }
    }

    // CUSTOMER: View invoice
    @GetMapping("/invoice/{paymentId}")
    public String viewInvoice(@PathVariable int paymentId, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        com.movieapp.filmplatform.model.Invoice invoice = paymentService.generateInvoice(paymentId);
        if (invoice == null) return "redirect:/my-rentals";

        com.movieapp.filmplatform.model.Payment payment = paymentService.getPaymentById(paymentId);
        if (payment.getCustomerId() != user.getId() && !user.getRole().equals("admin")) {
            return "redirect:/my-rentals";
        }

        model.addAttribute("invoice", invoice);
        model.addAttribute("user", user);
        return "customer/payment/invoice";
    }


    // CUSTOMER: View invoice by rental ID
    @GetMapping("/invoice/rental/{rentalId}")
    public String viewInvoiceByRental(@PathVariable int rentalId, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Rental rental = rentalService.getRentalById(rentalId);
        if (rental == null) return "redirect:/my-rentals";
        if (rental.getCustomerId() != user.getId() && !user.getRole().equals("admin")) {
            return "redirect:/my-rentals";
        }

        com.movieapp.filmplatform.model.Invoice invoice = paymentService.generateInvoiceByRental(rentalId);
        if (invoice == null) return "redirect:/my-rentals";

        model.addAttribute("invoice", invoice);
        model.addAttribute("user", user);
        return "customer/payment/invoice";
    }

    // CUSTOMER: Payment history
    @GetMapping("/payment-history")
    public String paymentHistory(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("invoices", paymentService.getInvoicesByCustomer(user.getId()));
        model.addAttribute("user", user);
        return "customer/payment/history";
    }

    // ADMIN: View all payments
    @GetMapping("/admin/payments")
    public String adminPayments(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("stats", paymentService.getPaymentStats());
        model.addAttribute("loggedInUser", user);
        return "admin/payment/payment-list";
    }

    // ADMIN: Process refund
    @PostMapping("/admin/payments/refund/{paymentId}")
    public String refundPayment(@PathVariable int paymentId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        try {
            paymentService.refundPayment(paymentId);
            redirectAttributes.addFlashAttribute("success", "Payment refunded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/payments";
    }
}