package edu.npic.smartBuilding.features.message;

import edu.npic.smartBuilding.base.MessageType;
import edu.npic.smartBuilding.domain.Device;
import edu.npic.smartBuilding.domain.Event;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.message.dto.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final EventRepository eventRepository;
    private final DeviceRepository deviceRepository;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, MessageRequest message) {
        log.info(message.toString() + " to " + to);
        messagingTemplate.convertAndSend("/topic/messages/" + to, message);
        if (message.messageType().equals(MessageType.SWITCH)) {
            Event event = eventRepository.findByDevice_Id(Integer.valueOf(message.deviceId()));
            event.setValue(message.value());
            eventRepository.save(event);
        }else if (message.messageType().equals(MessageType.POWER)) {
            try {
                double value = Double.parseDouble(message.value());
                if (value > 0) {
                    Device device = deviceRepository.findById(Integer.valueOf(message.deviceId())).orElse(null);
                    Event event = new Event();
                    event.setUuid(UUID.randomUUID().toString());
                    event.setDevice(device);
                    event.setCreatedAt(LocalDateTime.now());
                    event.setValue(String.valueOf(value));
                    eventRepository.save(event);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid value: " + message.value());
            }
        }else {
            Device device = deviceRepository.findById(Integer.valueOf(message.deviceId())).orElse(null);
            Event event = new Event();
            event.setUuid(UUID.randomUUID().toString());
            event.setDevice(device);
            event.setCreatedAt(LocalDateTime.now());
            event.setValue(String.valueOf(message.value()));
            eventRepository.save(event);
        }

    }

}
