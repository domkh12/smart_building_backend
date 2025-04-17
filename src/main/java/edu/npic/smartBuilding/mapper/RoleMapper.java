package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Role;
import edu.npic.smartBuilding.features.role.dto.RoleResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    List<RoleResponse> toRoleResponse(List<Role> roles);
}
