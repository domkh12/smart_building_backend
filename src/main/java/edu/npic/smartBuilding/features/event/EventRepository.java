package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Event findByDevice_Id(Integer id);

    @Query("select e from Event e where e.createdAt between ?1 and ?2")
    List<Event> findByCreatedAtBetween(LocalDateTime createdAtStart, LocalDateTime createdAtEnd);


    @Query("select e from Event e where e.createdAt between ?1 and ?2 and upper(e.device.name) = upper('POWER') and e.device.room.floor.building.id = ?3")
    List<Event> findPowerEventByBuildingId(LocalDateTime startDate, LocalDateTime endDate, Integer id);


    @Query("select e from Event e where e.device.id = ?1")
    Page<Event> findByDeviceId(Integer id, Pageable pageable);

    Optional<Event> findByDevice_Uuid(String uuid);

}
