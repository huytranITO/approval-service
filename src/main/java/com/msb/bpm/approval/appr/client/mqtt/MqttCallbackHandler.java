package com.msb.bpm.approval.appr.client.mqtt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

@Slf4j
@AllArgsConstructor
public class MqttCallbackHandler implements MqttCallback {

  private final MqttClient mqttClient;

  @Override
  public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
    try {
      log.info("[MQTT_RECONNECT] start");
      mqttClient.connect();
    } catch (MqttException e) {
      log.error("[MQTT_RECONNECT] connect fail");
    }
  }

  @Override
  public void mqttErrorOccurred(MqttException e) {

  }

  @Override
  public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

  }

  @Override
  public void deliveryComplete(IMqttToken iMqttToken) {

  }

  @Override
  public void connectComplete(boolean b, String s) {

  }

  @Override
  public void authPacketArrived(int i, MqttProperties mqttProperties) {

  }
}
