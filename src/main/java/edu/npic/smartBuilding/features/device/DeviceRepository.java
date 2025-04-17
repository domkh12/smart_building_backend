package edu.npic.smartBuilding.features.device;

import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.domain.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    long countByRoom_Id(Integer id);

    List<Device> findByRoom_Id(Integer id);

    @Query("""
            select d from Device d
            where upper(d.name) like upper(concat('%', ?1, '%')) and (d.deviceType.id in ?2 or ?2 is null) and (d.room.floor.building.id in ?3 or ?3 is null )""")
    Page<Device> filterDevice(String name, Collection<Integer> ids, Collection<Integer> buildings, Pageable pageable);


    List<Device> findByRoom_Uuid(String uuid);

    Optional<Device> findByUuid(String uuid);
}
