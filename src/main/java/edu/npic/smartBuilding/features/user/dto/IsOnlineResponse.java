package edu.npic.smartBuilding.features.user.dto;

import lombok.Builder;

@Builder
public record IsOnlineResponse(
        Integer id,
        Boolean isOnline
) {
}
