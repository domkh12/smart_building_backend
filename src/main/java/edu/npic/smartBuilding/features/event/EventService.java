package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.features.event.dto.EventResponse;
import org.springframework.data.domain.Page;

public interface EventService {
    Page<EventResponse> findByDeviceId(Integer id, int pageSize, int pageNo);
}
