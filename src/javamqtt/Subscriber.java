/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Subscriber implements IMqttMessageListener {

    public Subscriber(ClienteMQTT clienteMQTT, String topic, int qos) {
        clienteMQTT.subscribe(qos, this, topic);
    }

    @Override
    public void messageArrived(String topic, MqttMessage m) throws Exception {
        System.out.println("Mensagem  recebida:");
        System.out.println("\tTópico: " + topic);
        System.out.println("\tMensagem: " + new String(m.getPayload()));
        System.out.println("");
    }

}
