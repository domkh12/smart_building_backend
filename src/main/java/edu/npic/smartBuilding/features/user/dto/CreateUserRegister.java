package edu.npic.smartBuilding.features.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRegister(
        @NotBlank(message = "Full name is required!")
        String fullName,
        @Email(message = "Email is invalid!")
        @NotBlank(message = "Email is required!")
        String email,
        @NotBlank(message = "Password is required!")
        String password,
        @NotBlank(message = "Confirm password is required!")
        String confirmPassword
) {
}
