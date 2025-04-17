package edu.npic.smartBuilding.features.user.dto;

import java.time.LocalDateTime;

public record UserResponse(

        String uuid,
        String fullName,
        String email,
        String phoneNumber,
        LocalDateTime createdAt

) {
}
