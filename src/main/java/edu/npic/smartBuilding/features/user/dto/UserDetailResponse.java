package edu.npic.smartBuilding.features.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.npic.smartBuilding.features.gender.dto.GenderResponse;
import edu.npic.smartBuilding.features.role.dto.RoleResponse;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserDetailResponse(

        Integer id,
        String fullName,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        String address,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        List<RoleResponse> roles,
        List<RoomNameResponse> rooms,
        String profileImage,
        String bannerImage,
        Boolean isOnline,
        String status,
        GenderResponse gender,
        Boolean isVerified,
        Boolean isDeleted

) {
}
