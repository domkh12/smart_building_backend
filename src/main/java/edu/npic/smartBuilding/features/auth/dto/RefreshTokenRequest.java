package edu.npic.smartBuilding.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Token is required")
        String token
) {
}
