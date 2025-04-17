package edu.npic.smartBuilding.features.floor;

import edu.npic.smartBuilding.features.floor.dto.FloorNameResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorRequest;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    FloorResponse findFloorById(@PathVariable int id) {
        return floorService.findFloorById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    Page<FloorResponse> filterFloor(@RequestParam(required = false, defaultValue = "") String keywords,
                                    @RequestParam(required = false, defaultValue = "") List<Integer> buildingId,
                                    @RequestParam(required = false, defaultValue = "1") int pageNo,
                                    @RequestParam(required = false, defaultValue = "20") int pageSize){
        return floorService.filterFloor(keywords, buildingId, pageSize, pageNo);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    FloorResponse updateFloor(@PathVariable Integer id, @RequestBody @Valid FloorRequest floorRequest){
        return floorService.updateFloor(id, floorRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deleteFloor(@PathVariable Integer id) {
        floorService.deleteFloor(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    List<FloorNameResponse> findAllName(){
        return floorService.findAllName();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<FloorResponse> findAll(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ){
        return floorService.findAll(pageNo, pageSize);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    FloorResponse create(@Valid @RequestBody FloorRequest floorRequest){
        return floorService.create(floorRequest);
    }


}
