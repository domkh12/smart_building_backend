package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.event.dto.EventResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponse toEventResponse(Event event);

}
