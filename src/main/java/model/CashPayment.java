package com.movieapp.filmplatform.model;

public class CashPayment extends com.movieapp.filmplatform.model.Payment {
    private double amountTendered;
    private double change;

    public CashPayment() {
        super();
    }

    public CashPayment(double amountTendered) {
        this.amountTendered = amountTendered;
    }

    @Override
    public boolean processPayment() {
        if (amountTendered >= getAmount()) {
            setChange(amountTendered - getAmount());
            setStatus("COMPLETED");
            setInvoiceNumber(generateInvoiceNumber());
            return true;
        }
        setStatus("FAILED");
        return false;
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }

    @Override
    public String getPaymentDetails() {
        return "Cash Payment";
    }

    public double getAmountTendered() { return amountTendered; }
    public void setAmountTendered(double amountTendered) { this.amountTendered = amountTendered; }
    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }
}
