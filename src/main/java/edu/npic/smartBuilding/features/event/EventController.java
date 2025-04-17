package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.features.event.dto.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/devices/{id}")
    @ResponseStatus(HttpStatus.OK)
    Page<EventResponse> findByDeviceId(@PathVariable() Integer id,
                                     @RequestParam(required = false, defaultValue = "1") int pageNo,
                                     @RequestParam(required = false, defaultValue = "20") int pageSize) {
        return eventService.findByDeviceId(id, pageSize, pageNo);
    }

}
