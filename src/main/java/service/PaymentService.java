package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private static final String FILE_PATH = "data/payments.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final RentalService rentalService;
    private final UserService userService;

    public PaymentService(RentalService rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    // CREATE: Process full payment for rental
    public com.movieapp.filmplatform.model.Payment processPayment(int rentalId, com.movieapp.filmplatform.model.PaymentForm form) throws IOException {
        Rental rental = rentalService.getRentalById(rentalId);
        if (rental == null) throw new IllegalArgumentException("Rental not found");

        double amountToPay = rental.getRentalPrice();
        if (rental.getLateFee() > 0) {
            amountToPay += rental.getLateFee();
        }

        return processPayment(rentalId, form, amountToPay);
    }

    // CREATE: Process specific amount payment
    // Use this for rental extension, because extension should charge ONLY the extra days.
    public com.movieapp.filmplatform.model.Payment processPayment(int rentalId, com.movieapp.filmplatform.model.PaymentForm form, double amountToPay) throws IOException {
        Rental rental = rentalService.getRentalById(rentalId);
        if (rental == null) throw new IllegalArgumentException("Rental not found");
        if (amountToPay <= 0) throw new IllegalArgumentException("Payment amount must be greater than zero");

        User customer = userService.getUserById(rental.getCustomerId());
        if (customer == null) throw new IllegalArgumentException("Customer not found");

        com.movieapp.filmplatform.model.Payment payment;
        if ("CARD".equals(form.getPaymentMethod())) {
            if (form.getCardNumber() == null || form.getCardNumber().length() < 4) {
                throw new IllegalArgumentException("Card number must contain at least 4 digits");
            }

            payment = new com.movieapp.filmplatform.model.CardPayment();
            ((com.movieapp.filmplatform.model.CardPayment) payment).setCardLastFour(form.getCardNumber().substring(form.getCardNumber().length() - 4));
            ((com.movieapp.filmplatform.model.CardPayment) payment).setCardType(form.getCardType() != null ? form.getCardType() : "Visa");
        } else {
            payment = new com.movieapp.filmplatform.model.CashPayment();
            ((com.movieapp.filmplatform.model.CashPayment) payment).setAmountTendered(form.getCashAmount() != null ? form.getCashAmount() : amountToPay);
        }

        payment.setRentalId(rentalId);
        payment.setCustomerId(rental.getCustomerId());
        payment.setCustomerName(customer.getName());
        payment.setAmount(amountToPay);
        payment.setPaymentDate(LocalDateTime.now());

        // Polymorphism: CardPayment and CashPayment process differently
        boolean success = payment.processPayment();

        if (success) {
            List<com.movieapp.filmplatform.model.Payment> payments = getAllPayments();
            int newId = payments.isEmpty() ? 1 : payments.stream().mapToInt(com.movieapp.filmplatform.model.Payment::getPaymentId).max().orElse(0) + 1;
            payment.setPaymentId(newId);

            // IMPORTANT: One Rental = One Invoice number.
            // If this rental already has a completed payment, reuse the same invoice number.
            String existingInvoiceNumber = getInvoiceNumberByRental(rentalId);
            if (existingInvoiceNumber != null) {
                payment.setInvoiceNumber(existingInvoiceNumber);
            }

            String line = serializePayment(payment);
            FileHandler.appendLine(FILE_PATH, line);
        }

        return payment;
    }

    // READ: Get all payments
    public List<com.movieapp.filmplatform.model.Payment> getAllPayments() throws IOException {
        List<com.movieapp.filmplatform.model.Payment> payments = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            com.movieapp.filmplatform.model.Payment payment = deserializePayment(line);
            if (payment != null) payments.add(payment);
        }
        return payments;
    }

    // READ: Get payment by ID
    public com.movieapp.filmplatform.model.Payment getPaymentById(int paymentId) throws IOException {
        return getAllPayments().stream()
                .filter(p -> p.getPaymentId() == paymentId)
                .findFirst()
                .orElse(null);
    }

    // READ: Get payments by customer
    public List<com.movieapp.filmplatform.model.Payment> getPaymentsByCustomer(int customerId) throws IOException {
        return getAllPayments().stream()
                .filter(p -> p.getCustomerId() == customerId)
                .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
                .collect(Collectors.toList());
    }

    // READ: Get all payments for one rental
    public List<com.movieapp.filmplatform.model.Payment> getPaymentsByRental(int rentalId) throws IOException {
        return getAllPayments().stream()
                .filter(p -> p.getRentalId() == rentalId)
                .sorted(Comparator.comparing(com.movieapp.filmplatform.model.Payment::getPaymentDate))
                .collect(Collectors.toList());
    }

    // READ: Get first payment by rental. Kept for existing pages.
    public com.movieapp.filmplatform.model.Payment getPaymentByRental(int rentalId) throws IOException {
        return getPaymentsByRental(rentalId).stream()
                .findFirst()
                .orElse(null);
    }

    // READ: Get latest payment by rental
    public com.movieapp.filmplatform.model.Payment getLatestPaymentByRental(int rentalId) throws IOException {
        return getPaymentsByRental(rentalId).stream()
                .max(Comparator.comparing(com.movieapp.filmplatform.model.Payment::getPaymentDate))
                .orElse(null);
    }

    // READ: One invoice summary per rental for customer payment dashboard
    public List<com.movieapp.filmplatform.model.Invoice> getInvoicesByCustomer(int customerId) throws IOException {
        List<com.movieapp.filmplatform.model.Payment> completedPayments = getAllPayments().stream()
                .filter(p -> p.getCustomerId() == customerId)
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .collect(Collectors.toList());

        Map<Integer, List<com.movieapp.filmplatform.model.Payment>> paymentsByRental = completedPayments.stream()
                .collect(Collectors.groupingBy(com.movieapp.filmplatform.model.Payment::getRentalId));

        List<com.movieapp.filmplatform.model.Invoice> invoices = new ArrayList<>();
        for (Integer rentalId : paymentsByRental.keySet()) {
            com.movieapp.filmplatform.model.Invoice invoice = generateInvoiceByRental(rentalId);
            if (invoice != null) {
                invoices.add(invoice);
            }
        }

        invoices.sort((i1, i2) -> i2.getPaymentDate().compareTo(i1.getPaymentDate()));
        return invoices;
    }

    // UPDATE: Refund payment
    public com.movieapp.filmplatform.model.Payment refundPayment(int paymentId) throws IOException {
        List<com.movieapp.filmplatform.model.Payment> payments = getAllPayments();
        com.movieapp.filmplatform.model.Payment payment = payments.stream()
                .filter(p -> p.getPaymentId() == paymentId)
                .findFirst()
                .orElse(null);

        if (payment == null) throw new IllegalArgumentException("Payment not found");
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }

        payment.setStatus("REFUNDED");
        saveAllPayments(payments);
        return payment;
    }

    // UPDATE: Refund all completed payments for a rental
    public void refundPaymentsByRental(int rentalId) throws IOException {
        List<com.movieapp.filmplatform.model.Payment> payments = getAllPayments();
        boolean changed = false;

        for (com.movieapp.filmplatform.model.Payment payment : payments) {
            if (payment.getRentalId() == rentalId && "COMPLETED".equals(payment.getStatus())) {
                payment.setStatus("REFUNDED");
                changed = true;
            }
        }

        if (changed) {
            saveAllPayments(payments);
        }
    }

    // DELETE: Cancel/void payment
    public void voidPayment(int paymentId) throws IOException {
        List<com.movieapp.filmplatform.model.Payment> payments = getAllPayments();
        com.movieapp.filmplatform.model.Payment payment = payments.stream()
                .filter(p -> p.getPaymentId() == paymentId)
                .findFirst()
                .orElse(null);

        if (payment == null) throw new IllegalArgumentException("Payment not found");
        if ("COMPLETED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("Cannot void completed payment. Process a refund instead.");
        }

        payment.setStatus("VOIDED");
        saveAllPayments(payments);
    }

    // Generate invoice using payment ID, but invoice total is calculated for the whole rental.
    public com.movieapp.filmplatform.model.Invoice generateInvoice(int paymentId) throws IOException {
        com.movieapp.filmplatform.model.Payment payment = getPaymentById(paymentId);
        if (payment == null) return null;
        return generateInvoiceByRental(payment.getRentalId());
    }

    // Generate one invoice for one rental by summing all completed payments.
    public com.movieapp.filmplatform.model.Invoice generateInvoiceByRental(int rentalId) throws IOException {
        Rental rental = rentalService.getRentalById(rentalId);
        if (rental == null) return null;

        List<com.movieapp.filmplatform.model.Payment> completedPayments = getPaymentsByRental(rentalId).stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .collect(Collectors.toList());

        if (completedPayments.isEmpty()) return null;

        com.movieapp.filmplatform.model.Payment firstPayment = completedPayments.get(0);
        com.movieapp.filmplatform.model.Payment latestPayment = completedPayments.stream()
                .max(Comparator.comparing(com.movieapp.filmplatform.model.Payment::getPaymentDate))
                .orElse(firstPayment);

        double totalPaid = completedPayments.stream()
                .mapToDouble(com.movieapp.filmplatform.model.Payment::getAmount)
                .sum();

        String methods = completedPayments.stream()
                .map(p -> p.getPaymentMethod() + " - " + p.getPaymentDetails())
                .distinct()
                .collect(Collectors.joining(", "));

        com.movieapp.filmplatform.model.Invoice invoice = new com.movieapp.filmplatform.model.Invoice();
        invoice.setInvoiceNumber(firstPayment.getInvoiceNumber());
        invoice.setRentalId(rentalId);
        invoice.setCustomerName(firstPayment.getCustomerName());
        invoice.setMovieTitle(rental.getMovieTitle());
        invoice.setRentalPrice(rental.getRentalPrice());
        invoice.setLateFee(rental.getLateFee());
        invoice.setTotalAmount(totalPaid);
        invoice.setPaymentMethod(methods);
        invoice.setPaymentDate(latestPayment.getPaymentDate());
        invoice.setStatus("COMPLETED");

        return invoice;
    }

    // Get payment statistics
    public com.movieapp.filmplatform.model.PaymentStats getPaymentStats() throws IOException {
        List<com.movieapp.filmplatform.model.Payment> payments = getAllPayments();

        com.movieapp.filmplatform.model.PaymentStats stats = new com.movieapp.filmplatform.model.PaymentStats();
        stats.setTotalPayments(payments.size());
        stats.setTotalRevenue(payments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .mapToDouble(com.movieapp.filmplatform.model.Payment::getAmount).sum());
        stats.setRefundedAmount(payments.stream()
                .filter(p -> "REFUNDED".equals(p.getStatus()))
                .mapToDouble(com.movieapp.filmplatform.model.Payment::getAmount).sum());
        stats.setPendingPayments((int) payments.stream()
                .filter(p -> "PENDING".equals(p.getStatus())).count());

        return stats;
    }

    private String getInvoiceNumberByRental(int rentalId) throws IOException {
        return getAllPayments().stream()
                .filter(p -> p.getRentalId() == rentalId)
                .filter(p -> p.getInvoiceNumber() != null && !p.getInvoiceNumber().isEmpty())
                .map(com.movieapp.filmplatform.model.Payment::getInvoiceNumber)
                .findFirst()
                .orElse(null);
    }

    // Helper: Serialize payment
    private String serializePayment(com.movieapp.filmplatform.model.Payment payment) {
        String base = String.join("|",
                String.valueOf(payment.getPaymentId()),
                String.valueOf(payment.getRentalId()),
                String.valueOf(payment.getCustomerId()),
                payment.getCustomerName(),
                String.valueOf(payment.getAmount()),
                payment.getPaymentDate().format(DATE_FORMATTER),
                payment.getStatus(),
                payment.getInvoiceNumber() != null ? payment.getInvoiceNumber() : "",
                payment.getPaymentMethod()
        );

        if (payment instanceof com.movieapp.filmplatform.model.CardPayment) {
            com.movieapp.filmplatform.model.CardPayment cp = (com.movieapp.filmplatform.model.CardPayment) payment;
            return base + "|" + cp.getCardLastFour() + "|" + cp.getCardType() + "|";
        } else if (payment instanceof com.movieapp.filmplatform.model.CashPayment) {
            com.movieapp.filmplatform.model.CashPayment cp = (com.movieapp.filmplatform.model.CashPayment) payment;
            return base + "|||" + cp.getAmountTendered() + "|" + cp.getChange();
        }
        return base;
    }

    // Helper: Deserialize payment
    private com.movieapp.filmplatform.model.Payment deserializePayment(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) return null;

        try {
            String paymentMethod = parts[8];
            com.movieapp.filmplatform.model.Payment payment;

            if ("Card".equals(paymentMethod)) {
                com.movieapp.filmplatform.model.CardPayment cp = new com.movieapp.filmplatform.model.CardPayment();
                if (parts.length > 10) {
                    cp.setCardLastFour(parts[9]);
                    cp.setCardType(parts[10]);
                }
                payment = cp;
            } else {
                com.movieapp.filmplatform.model.CashPayment cp = new com.movieapp.filmplatform.model.CashPayment();
                if (parts.length > 12) {
                    cp.setAmountTendered(Double.parseDouble(parts[11]));
                    cp.setChange(Double.parseDouble(parts[12]));
                }
                payment = cp;
            }

            payment.setPaymentId(Integer.parseInt(parts[0]));
            payment.setRentalId(Integer.parseInt(parts[1]));
            payment.setCustomerId(Integer.parseInt(parts[2]));
            payment.setCustomerName(parts[3]);
            payment.setAmount(Double.parseDouble(parts[4]));
            payment.setPaymentDate(LocalDateTime.parse(parts[5], DATE_FORMATTER));
            payment.setStatus(parts[6]);
            payment.setInvoiceNumber(parts[7].isEmpty() ? null : parts[7]);

            return payment;
        } catch (Exception e) {
            return null;
        }
    }

    // Save all payments
    private void saveAllPayments(List<com.movieapp.filmplatform.model.Payment> payments) throws IOException {
        List<String> lines = new ArrayList<>();
        for (com.movieapp.filmplatform.model.Payment payment : payments) {
            lines.add(serializePayment(payment));
        }
        FileHandler.writeLines(FILE_PATH, lines);
    }
}
