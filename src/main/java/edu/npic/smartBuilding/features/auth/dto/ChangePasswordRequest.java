package edu.npic.smartBuilding.features.auth.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
