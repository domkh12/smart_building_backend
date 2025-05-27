package edu.npic.smartBuilding.features.room;

import edu.npic.smartBuilding.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE events SET device_id = null WHERE device_id IN (SELECT id FROM devices WHERE room_id = ?1); DELETE FROM users_rooms WHERE room_id IN (SELECT id FROM rooms WHERE id = ?1); DELETE FROM devices WHERE room_id = ?1; DELETE FROM rooms WHERE id = ?1", nativeQuery = true)
    void deleteByRoomById(Integer roomId);

    @Query("select count(r) from Room r where r.createdAt between ?1 and ?2")
    long roomCountByDate(LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    @Query("select r from Room r where r.id in ?1")
    Page<Room> findRoomByIds(Collection<Long> ids, Pageable pageable);

    @Query("select count(r) from Room r where r.id = ?1")
    long countRoomByRoomId(Integer id);

    Optional<Room> findByDevices_Id(Integer id);

    List<Room> findByIdIn(Collection<Integer> ids);

    @Query("""
            select r from Room r where upper(r.name) like upper(concat('%', ?1, '%')) and (r.floor.building.id in ?2 or ?2 is null)
            """)
    Page<Room> roomFilter(String name, Collection<Integer> ids, Pageable pageable);

}
