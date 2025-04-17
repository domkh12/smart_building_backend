package edu.npic.smartBuilding.features.room;

import edu.npic.smartBuilding.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {



    @Query("select count(r) from Room r where r.id = ?1")
    long countRoomByRoomId(Integer id);

    Optional<Room> findByDevices_Id(Integer id);

    List<Room> findByIdIn(Collection<Integer> ids);

    @Query("""
            select r from Room r where upper(r.name) like upper(concat('%', ?1, '%')) and (r.floor.building.id in ?2 or ?2 is null)
            """)
    Page<Room> roomFilter(String name, Collection<Integer> ids, Pageable pageable);

}
