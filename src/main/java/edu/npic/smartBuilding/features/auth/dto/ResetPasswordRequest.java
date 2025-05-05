package edu.npic.smartBuilding.features.auth.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}

