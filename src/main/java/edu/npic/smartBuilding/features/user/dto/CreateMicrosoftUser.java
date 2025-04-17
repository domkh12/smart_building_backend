package edu.npic.smartBuilding.features.user.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateMicrosoftUser(
        String uuid,
        String email,
        String fullName,
        List<String> roles
) {
}
