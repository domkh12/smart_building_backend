package edu.npic.smartBuilding.features.role;

import edu.npic.smartBuilding.domain.Role;
import edu.npic.smartBuilding.features.role.dto.RoleResponse;
import edu.npic.smartBuilding.mapper.RoleMapper;
import edu.npic.smartBuilding.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final AuthUtil authUtil;

    @Override
    public List<RoleResponse> findAll() {
        boolean isAdmin = authUtil.isAdminLoggedUser();

        if (isAdmin){
            List<Role> roles = roleRepository.findRoleUser();
            return roleMapper.toRoleResponse(roles);
        }
        List<Role> roles = roleRepository.findAll();

        return roleMapper.toRoleResponse(roles);
    }
}
