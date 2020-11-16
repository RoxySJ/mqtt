/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamqtt;

import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class ClienteMQTT implements MqttCallbackExtended {

    private final String server;
    private MqttClient client;
    private final MqttConnectOptions mqttOp;

    public ClienteMQTT(String server, String user, String password) {
        this.server = server;

        mqttOp = new MqttConnectOptions();
        mqttOp.setMaxInflight(200);
        mqttOp.setConnectionTimeout(3);
        mqttOp.setKeepAliveInterval(10);
        mqttOp.setAutomaticReconnect(true);
        mqttOp.setCleanSession(false);

        if (user != null && password != null) {
            mqttOp.setUserName(user);
            mqttOp.setPassword(password.toCharArray());
        }
    }

    public IMqttToken subscribe(int qos, IMqttMessageListener MQTTmsgHandler, String... topics) {
        if (client == null || topics.length == 0) {
            return null;
        }
        int size = topics.length;
        int[] qoss = new int[size];
        IMqttMessageListener[] listners = new IMqttMessageListener[size];

        for (int i = 0; i < size; i++) {
            qoss[i] = qos;
            listners[i] = MQTTmsgHandler;
        }
        try {
            return client.subscribeWithResponse(topics, qoss, listners);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro na inscrição nos tópicos %s - %s", Arrays.asList(topics), ex));
            return null;
        }
    }

    public void unsubscribe(String... topics) {
        if (client == null || !client.isConnected() || topics.length == 0) {
            return;
        }
        try {
            client.unsubscribe(topics);
        } catch (MqttException ex) {
            System.out.println(String.format("Erro na  desinscrição dos tópicos %s - %s", Arrays.asList(topics), ex));
        }
    }

    public void iniciar() {
        try {
            System.out.println("Conectando-se ao broker MQTT em " + server);
            client = new MqttClient(server, String.format("cliente_java_%d", System.currentTimeMillis()), new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
            client.setCallback(this);
            client.connect(mqttOp);
        } catch (MqttException ex) {
            System.out.println("Erro ao se conectar ao broker MQTT " + server + " - " + ex);
        }
    }

    public void finalizar() {
        if (client == null || !client.isConnected()) {
            return;
        }
        try {
            client.disconnect();
            client.close();
        } catch (MqttException ex) {
            System.out.println("Erro na desconexão do broker MQTT - " + ex);
        }
    }

    public void publicar(String topic, byte[] payload, int qos) {
        publicar(topic, payload, qos, false);
    }

    public synchronized void publicar(String topic, byte[] payload, int qos, boolean retained) {
        try {
            if (client.isConnected()) {
                client.publish(topic, payload, qos, retained);
                System.out.println(String.format("Mensagem no tópico %s     . ", topic));
            } else {
                System.out.println("Cliente desconectado/ Erro ao publicar o tópico " + topic);
            }
        } catch (MqttException ex) {
            System.out.println("Erro ao publicar " + topic + " - " + ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("Conexão ao broker  perdida -" + thrwbl);
    }

    @Override
    public void connectComplete(boolean reconnect, String server) {
        System.out.println("Cliente MQTT " + (reconnect ? "reconectado" : "conectado") + " com o broker " + server);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
    }

}
