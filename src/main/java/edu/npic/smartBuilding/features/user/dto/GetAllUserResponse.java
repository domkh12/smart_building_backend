package edu.npic.smartBuilding.features.user.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.Map;

@Builder
public record GetAllUserResponse(
        Page<UserDetailResponse> allUsers,
        Map<String, Integer> statusCount
) {
}
