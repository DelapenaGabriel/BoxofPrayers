package com.server.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * RegisterUserDto is a class used to hold the registration information for a new user
 * that's sent from the client to the server for the register endpoint.
 *
 * The acronym DTO is being used for "data transfer object". It means that this type of
 * class is specifically created to transfer data between the client and the server.
 */
public class RegisterUserDto {

    @NotEmpty
    @Size(max=50)
    private String name;
    @NotEmpty
    @Size(max=50)
    private String displayName;
    @NotEmpty
    @jakarta.validation.constraints.Email
    private String email;
    @NotEmpty
    @Size(min=6, max=100)
    private String password;
    @NotEmpty
    private String confirmPassword;
    private String role;

    public RegisterUserDto(String name, String displayName, String email, String password, String confirmPassword, String role) {
        this.name = name;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getRole() {
        if (role == null || role.trim().isEmpty()) {
            return "ROLE_USER";
        }
        return role;
    }

    public boolean isPasswordsMatch(){
        return password.equals(confirmPassword);
    }
}
