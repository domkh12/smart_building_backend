package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.event.dto.EventResponse;
import edu.npic.smartBuilding.mapper.EventMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EvenServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EvenServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Page<EventResponse> findByDeviceId(Integer id, int pageSize, int pageNo) {
        if (pageNo < 1 || pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page size or page no");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Event> events = eventRepository.findByDeviceId(id, pageRequest);

        return events.map(eventMapper::toEventResponse);
    }

}
