package edu.npic.smartBuilding.features.role;

import edu.npic.smartBuilding.features.role.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> findAll();
}
