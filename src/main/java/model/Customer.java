package model;

package com.movieapp.filmplatform.model;

public class Customer extends User {

    public Customer() {}

    public Customer(int id, String name, String email, String password, boolean active) {
        super(id, name, email, password, active);
    }

    @Override
    public String getRole() {
        return "customer";
    }

    @Override
    public boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }
}
