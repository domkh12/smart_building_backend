package edu.npic.smartBuilding.features.deviceType;

import edu.npic.smartBuilding.domain.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Integer> {

    Optional<DeviceType> findByUuid(String uuid);

    boolean existsByName(String name);
}
