package edu.npic.smartBuilding.features.message;

import com.nimbusds.jose.shaded.gson.Gson;
import edu.npic.smartBuilding.features.message.dto.MessageRequest;
import edu.npic.smartBuilding.util.MqttGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MqttGateway mqttGateway;
    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, MessageRequest message) {
        log.info(message.toString() + " to " + to);
        messagingTemplate.convertAndSend("/topic/messages/" + to, List.of(message));
        String payload = new Gson().toJson(message);
        String mqttTopic = "/room/" + to + "/control";
        mqttGateway.publish(mqttTopic, payload);
    }

}
