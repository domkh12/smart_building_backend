package edu.npic.smartBuilding.features.auth.dto;

import lombok.Builder;

@Builder
public record MessageResponse(
        String message
) {
}
