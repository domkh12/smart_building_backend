package edu.npic.smartBuilding.features.room;

import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomRequest;
import edu.npic.smartBuilding.features.room.dto.RoomResponse;
import edu.npic.smartBuilding.features.room.dto.RoomResponseGetById;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    RoomResponseGetById getRoomById(@PathVariable Integer id) {
        return roomService.getRoomById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    Page<RoomResponse> roomFilter(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "") List<Integer> buildingId,
            @RequestParam(required = false, defaultValue = "") String keywords
    ){
        return roomService.roomFilter(pageNo, pageSize, buildingId, keywords);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    RoomResponse update(@PathVariable Integer id, @RequestBody RoomRequest roomRequest){
        return roomService.update(id, roomRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/names")
    @ResponseStatus(HttpStatus.OK)
    List<RoomNameResponse> findAllName(){
        return roomService.findAllName();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RoomResponse create(@Valid @RequestBody RoomRequest roomRequest){
        return roomService.create(roomRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<RoomResponse> findAll(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ){
        return roomService.findAll(pageNo, pageSize);
    }

}
