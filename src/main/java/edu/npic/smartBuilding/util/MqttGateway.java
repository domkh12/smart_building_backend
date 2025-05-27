package edu.npic.smartBuilding.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttGateway {

    private final MessageChannel mqttOutboundChannel;

    public void publish(String topic, String payload) {
        log.info("Publishing to MQTT topic [{}]: {}", topic, payload);
        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .build()
        );
    }

}
