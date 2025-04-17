package edu.npic.smartBuilding.features.building;

import edu.npic.smartBuilding.features.building.dto.BuildingNameResponse;
import edu.npic.smartBuilding.features.building.dto.BuildingRequest;
import edu.npic.smartBuilding.features.building.dto.BuildingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    BuildingResponse getBuildingById(@PathVariable Integer id) {
        return buildingService.getBuildingById(id);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    Page<BuildingResponse> filterBuilding(
            @RequestParam String keywords,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "1") int pageNo
            ) {
        return buildingService.filterBuilding(keywords, pageSize, pageNo);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Integer id) {
        buildingService.delete(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    BuildingResponse update(@PathVariable Integer id, @Valid @RequestBody BuildingRequest buildingRequest) {
        return buildingService.update(id, buildingRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    List<BuildingNameResponse> findAllName(){
        return buildingService.findAllName();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BuildingResponse create(@Valid  @RequestBody BuildingRequest buildingRequest) {
        return buildingService.create(buildingRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<BuildingResponse> findAll(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
            ){
        return buildingService.findAll(pageNo, pageSize);
    }

}
