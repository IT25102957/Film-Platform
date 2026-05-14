package model;

package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private boolean active;

    public abstract String getRole();
    public abstract boolean validatePassword(String password);
}
