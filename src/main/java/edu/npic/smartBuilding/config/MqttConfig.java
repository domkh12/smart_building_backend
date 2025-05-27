package edu.npic.smartBuilding.config;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.features.device.DeviceService;
import edu.npic.smartBuilding.features.event.EventService;
import edu.npic.smartBuilding.features.message.dto.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Type;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MqttConfig {
    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceService deviceService;
    private final EventService eventService;

    @Value("${mqtt.server-url}")
    private String serverUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttPahoClientFactory mqttClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{serverUrl});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(90);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        factory.setConnectionOptions(options);

        return factory;
    }

    // Subscriber
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound(MqttPahoClientFactory clientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "spring-client", clientFactory, "/room/+/status", "/room/+/data", "/room/+/control");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            String payload = (String) message.getPayload();

            log.info("Received message from topic [{}]: {}", topic, payload);

            // Extract room ID from topic
            String[] tokens = topic.split("/");
            if (tokens.length >= 3) {
                String roomId = tokens[2]; // tokens[0] = "", tokens[1] = "room", tokens[2] = roomId
                String subTopic = tokens[3]; // "status" or "data"

                if ("data".equals(subTopic)) {
                    Type listType = new TypeToken<List<MessageRequest>>() {}.getType();
                    List<MessageRequest> messageRequests = new Gson().fromJson(payload, listType);
                    messagingTemplate.convertAndSend("/topic/messages/" + roomId, messageRequests);
                    for (MessageRequest messageRequest : messageRequests) {
                        eventService.updateEventByMessageDevice(messageRequest);
                    }
                }

                if ("control".equals(subTopic)) {
                    try {
                        MessageRequest messageRequest = new Gson().fromJson(payload, MessageRequest.class);
                        eventService.updateEventByMessageDeviceControl(messageRequest);
                    } catch (Exception e) {
                        log.warn("Failed to parse payload to MessageRequest: {}", payload, e);
                    }
                }

                if ("status".equals(subTopic)) {
                    if ("online".equals(payload)) {
                        try {
                            int numericRoomId = Integer.parseInt(roomId);
                            deviceService.updateStatusDeviceByRoomId(numericRoomId, DeviceStatus.Active);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid roomId [{}], not a number. Skipping update.", roomId);
                        }

                    } else if ("offline".equals(payload)) {
                        try {
                            int numericRoomId = Integer.parseInt(roomId);
                            deviceService.updateStatusDeviceByRoomId(numericRoomId, DeviceStatus.Inactive);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid roomId [{}], not a number. Skipping update.", roomId);
                        }
                    }
                }
            }
        };
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // Publisher
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory clientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("clientId-publisher", clientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("/topic");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}
