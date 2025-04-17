package edu.npic.smartBuilding.features.user.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record CreateUser(
        @NotBlank(message = "Full name is required!")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,
        @NotNull(message = "Gender ID is required")
        Integer genderId,
        @NotBlank(message = "Email is required")
        @Email(message = "Email is not a valid format")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Confirm password is required")
        String confirmPassword,
        @NotBlank(message = "Phone number is required!")
        String phoneNumber,
        @Size(max = 2048, message = "Profile image URL cannot exceed 2048 characters")
        String profileImage,
        String address,
        List<Integer> roleId,
        List<Integer> roomId,
        Boolean isVerified
) {
}
