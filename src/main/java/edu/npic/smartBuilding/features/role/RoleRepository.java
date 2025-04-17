package edu.npic.smartBuilding.features.role;

import edu.npic.smartBuilding.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where upper(r.name) = upper('USER')")
    List<Role> findRoleUser();

    List<Role> findByIdIn(Collection<Integer> ids);

    Optional<Role> findByName(String name);

    Optional<Role> findByUuid(String uuid);

    Optional<Role> findByNameIgnoreCase(String name);


}
