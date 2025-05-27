package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.features.event.dto.EventResponse;
import edu.npic.smartBuilding.features.message.dto.MessageRequest;
import org.springframework.data.domain.Page;

public interface EventService {

    void updateEventByMessageDeviceControl(MessageRequest message);

    void updateEventByMessageDevice(MessageRequest message);

    Page<EventResponse> findByDeviceId(Integer id, int pageSize, int pageNo);
}
