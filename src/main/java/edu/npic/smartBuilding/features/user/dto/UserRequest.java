package edu.npic.smartBuilding.features.user.dto;

import edu.npic.smartBuilding.base.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserRequest(
        String uuid,
        String firstName,
        String lastName,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        Boolean isVerified,
        List<String> roleNames,
        String profileImage,
        Status status,
        Boolean isDeleted
) {
}
