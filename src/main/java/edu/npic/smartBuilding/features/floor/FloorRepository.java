package edu.npic.smartBuilding.features.floor;

import edu.npic.smartBuilding.domain.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer> {

    @Query("select f from Floor f left join f.rooms rooms where rooms.id in ?1")
    List<Floor> findByRooms_IdIn(Collection<Long> ids);

    long countByRooms_Id(Integer id);

    @Query("""
            select f from Floor f where upper(f.name) like upper(concat('%', ?1, '%')) and (f.building.id in ?2 or ?2 is null)
            """)
    Page<Floor> filterFloor(String name, Collection<Integer> ids, Pageable pageable);

}
