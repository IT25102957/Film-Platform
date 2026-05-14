package model;

package com.movieapp.filmplatform.model;

public class Admin extends User {

    public Admin() {}

    public Admin(int id, String name, String email, String password, boolean active) {
        super(id, name, email, password, active);
    }

    @Override
    public String getRole() {
        return "admin";
    }

    @Override
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        return hasDigit && hasUpper;
    }
}
