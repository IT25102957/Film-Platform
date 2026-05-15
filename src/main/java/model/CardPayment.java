package com.movieapp.filmplatform.model;

public class CardPayment extends com.movieapp.filmplatform.model.Payment {
    private String cardLastFour;
    private String cardType; // Visa, Mastercard, etc.

    public CardPayment() {
        super();
    }

    public CardPayment(String cardLastFour, String cardType) {
        this.cardLastFour = cardLastFour;
        this.cardType = cardType;
    }

    @Override
    public boolean processPayment() {
        // Simple validation - card number must have 4 digits
        if (cardLastFour != null && cardLastFour.matches("\\d{4}")) {
            setStatus("COMPLETED");
            setInvoiceNumber(generateInvoiceNumber());
            return true;
        }
        setStatus("FAILED");
        return false;
    }

    @Override
    public String getPaymentMethod() {
        return "Card";
    }

    @Override
    public String getPaymentDetails() {
        return cardType + " ****" + cardLastFour;
    }

    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
}

