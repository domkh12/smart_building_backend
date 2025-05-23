package edu.npic.smartBuilding.features.building;

import edu.npic.smartBuilding.domain.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {

    long countByFloors_Rooms_Id(Integer id);

    @Query("select b from Building b where upper(b.name) like upper(concat('%', ?1, '%'))")
    Page<Building> filterBuilding(String name, Pageable pageable);

    Boolean existsByName(String name);

    @Query("""
            select (count(b) > 0) from Building b inner join b.floors floors
            where b.uuid = ?1 and upper(floors.name) = upper(?2)""")
    Boolean existFloorNameInBuilding(String uuid, String name);


}
