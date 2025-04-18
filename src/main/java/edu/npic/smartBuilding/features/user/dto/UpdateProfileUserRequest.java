package edu.npic.smartBuilding.features.user.dto;

import java.time.LocalDate;

public record UpdateProfileUserRequest(
        String fullName,
        Integer genderId,
        String address,
        String phoneNumber,
        LocalDate dateOfBirth,
        String profileImage
) {
}
