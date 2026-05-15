package model;

package com.movieapp.filmplatform.model;

import lombok.Data;

@Data
public class ProfileUpdateForm {
    private String name;
    private String email;
    private String password;
}
