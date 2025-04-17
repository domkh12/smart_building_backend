package edu.npic.smartBuilding.features.role.dto;

import lombok.Builder;

@Builder
public record RoleResponse(
        Integer id,
        String name
) {
}
