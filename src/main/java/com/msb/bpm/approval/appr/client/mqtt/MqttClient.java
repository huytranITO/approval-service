package com.msb.bpm.approval.appr.client.mqtt;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttClient implements PublishMessage {

  private final MqttClientProperties properties;
  private MqttAsyncClient asyncClient;
  private MemoryPersistence persistence;
  private MqttConnectionOptions connectionOptions;
  private Integer numberOfReconnect = 0;

  @PostConstruct
  private void initConnection() throws MqttException {
    log.info("Init connection...");
    persistence = new MemoryPersistence();
    asyncClient = new MqttAsyncClient(properties.getServerUrl(), getClientId(), persistence);

    MqttConnectionOptionsBuilder conOptsBuilder = new MqttConnectionOptionsBuilder();
    connectionOptions = conOptsBuilder
        .serverURI(properties.getServerUrl())
        .cleanStart(true)
        .connectionTimeout(properties.getConnectionTimeout())
        .username(properties.getUsername())
        .password(properties.getPassword().getBytes())
        .automaticReconnect(true)
        .build();

    asyncClient.setCallback(new MqttCallbackHandler(this));
    connect();
  }

  public void connect() throws MqttException {
//    if (numberOfReconnect <= properties.getMaxNumberOfReconnect()) {
      asyncClient.connect(connectionOptions, null, new MqttV5ActionListener());
//    }
  }

  @Override
  public void publish(String topic, String message) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(properties.getQos());
    try {
      asyncClient.publish(topic, mqttMessage);
    } catch (MqttException e) {
      log.error("[PUBLISH_MESSAGE_MQTT_FAIL]", e);
      throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
    }
  }

  public MqttAsyncClient getAsyncClient() {
    return this.asyncClient;
  }

  static class MqttV5ActionListener implements MqttActionListener {

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
      log.info("Mqtt connected.");
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable exception) {
      log.error("Failed to Connect: " + exception.getLocalizedMessage(), exception);
    }
  }

  private String getClientId() {
    Map<String, String> env = System.getenv();
    return properties.getClientId() + "-" + (env.containsKey("COMPUTERNAME")
        ? env.get("COMPUTERNAME")
        : env.get("HOSTNAME"));
  }
}
