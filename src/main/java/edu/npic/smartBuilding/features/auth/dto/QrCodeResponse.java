package edu.npic.smartBuilding.features.auth.dto;

import lombok.Builder;

@Builder
public record QrCodeResponse(
        String qrCodeUrl
) {
}
