package edu.npic.smartBuilding.features.event;

import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.event.dto.EventResponse;
import edu.npic.smartBuilding.features.message.dto.MessageRequest;
import edu.npic.smartBuilding.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvenServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final DeviceRepository deviceRepository;

    @Override
    public void updateEventByMessageDeviceControl(MessageRequest message) {
        Event event = new Event();
        event.setUuid(UUID.randomUUID().toString());

        if (message.messageType().equalsIgnoreCase("SWITCH") && message.status() == null) {
            try {
                int switchValue = Integer.parseInt(message.value());

                if (switchValue != 0 && switchValue != 1) {
                    log.warn("Switch value {} is invalid. Only 0 or 1 are accepted", switchValue);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(switchValue));
                log.info("Received control message: {}", message);
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid switch format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Switch value must be a valid number (0 or 1)");
            }
        }
    }

    @Override
    public void updateEventByMessageDevice(MessageRequest message) {

        Event event = new Event();
        event.setUuid(UUID.randomUUID().toString());

        if (message == null) {
            log.warn("Message is empty");
            return;
        }

        if (message.messageType().equalsIgnoreCase("TEMPERATURE")) {
            try {
                double temperature = Double.parseDouble(message.value());

                if (temperature < -50 || temperature > 100) {
                    log.warn("Temperature value {} is out of valid range (-50°C to 100°C)", temperature);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(temperature));
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid temperature format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Temperature value must be a valid number");
            }

        } else if (message.messageType().equalsIgnoreCase("HUMIDITY")) {
            try {
                double humidity = Double.parseDouble(message.value());

                if (humidity < 0 || humidity > 100) {
                    log.warn("Humidity value {} is out of valid range (0% to 100%)", humidity);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(humidity));
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid humidity format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Humidity value must be a valid number");
            }
        } else if (message.messageType().equalsIgnoreCase("PM2_5")) {
            try {
                double pm25 = Double.parseDouble(message.value());

                if (pm25 < 0 || pm25 > 1000) {
                    log.warn("PM2.5 value {} is out of valid range (0 to 1000 µg/m³)", pm25);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(pm25));
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid PM2.5 format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "PM2.5 value must be a valid number");
            }

        } else if (message.messageType().equalsIgnoreCase("POWER")) {
            try {
                double power = Double.parseDouble(message.value());

                if (power <= 0 || power > 10000) {  // assuming max 10kW for a typical building unit
                    log.warn("Power value {} is out of valid range (0W to 10000W)", power);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(power));
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid power format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Power value must be a valid number");
            }
        } else if (message.messageType().equalsIgnoreCase("SWITCH")) {
            try {
                int switchValue = Integer.parseInt(message.value());

                if (switchValue != 0 && switchValue != 1) {
                    log.warn("Switch value {} is invalid. Only 0 or 1 are accepted", switchValue);
                    return;
                }
                event.setDevice(deviceRepository.findById(Integer.valueOf(message.deviceId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")));
                event.setCreatedAt(LocalDateTime.now());
                event.setValue(String.valueOf(switchValue));
                log.info("Received control message: {}", message);
                eventRepository.save(event);

            } catch (NumberFormatException e) {
                log.error("Invalid switch format: {}", message.value());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Switch value must be a valid number (0 or 1)");
            }
        }
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
