package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    MqttClient client;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv=findViewById(R.id.textview);

        try {
            client = new MqttClient("tcp://broker.emqx.io:1883", "client01", new MemoryPersistence());
            client.setCallback(this);
            client.connect();
            client.subscribe("topic01");
            publishMessage("hellooo");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String payload) throws MqttException{
        if (client.isConnected()==false){
            client.connect();
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(2);
        message.setRetained(false);
        client.publish("topic01",message);

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload(), StandardCharsets.UTF_8);
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "Message Arrived: " + msg, Toast.LENGTH_SHORT).show();
            tv.setText(msg);
        });

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}