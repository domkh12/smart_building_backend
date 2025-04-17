package edu.npic.smartBuilding.features.user.dto;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.List;

public record UpdateUserRequest(
        String fullName,
        @Email(message = "email wrong format")
        String email,
        Integer genderId,
        String address,
        LocalDate dateOfBirth,
        String phoneNumber,
        String profileImage,
        Boolean isDeleted,
        Boolean isVerified,
        List<Integer> roleId,
        List<Integer> roomId
) {
}
