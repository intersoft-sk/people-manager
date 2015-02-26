/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ebbitsproject.peoplemanager.utils;

import eu.ebbitsproject.peoplemanager.PersonDynamicStore;
import eu.ebbitsproject.peoplemanager.RuleEngineImpl;
import eu.ebbitsproject.peoplemanager.model.Event;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author glukac
 */
public class MQTTProxy {

    private static volatile MQTTProxy instance = null;

    private final String SERVER_URL = "ssl://ebbits.fit.fraunhofer.de:8883";
    private final String USERNAME = "ebbitsM48Review";
    private final String PASSWORD = "HEQ8Lo5sFsKWrUtG";
    private final String CA_FILE_PATH = "ca.crt";
    private final String TOPIC = "/awareness/error";
    private final String NOTIFICATION_TOPIC = "/awareness/recovery";

    private MqttClient client = null;
    
    private MQTTProxy() {
        try {
            this.client = new MqttClient(SERVER_URL, "PeopleManagerClient" , null);
            this.client.setCallback(new MyCallback());
            MqttConnectOptions options = new MqttConnectOptions();
            
            // Authenticate
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            // Establish a secure socket connection
            SocketFactory socketFactory = SslUtil.getSocketFactory(CA_FILE_PATH);
            options.setSocketFactory(socketFactory);

            // Connect and subscribe
            this.client.connect(options);
            this.client.subscribe(NOTIFICATION_TOPIC, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized MQTTProxy getInstance() {
        if (instance == null) {
            instance = new MQTTProxy();
        }
        return instance;
    }

    public void sendMessage(Event e) {
        
        JSONObject obj = new JSONObject();
        obj.put("eventId", e.getId());
        obj.put("type", e.getType());        
        obj.put("sensorId", e.getProps().get("sensorid"));
        obj.put("timestamp", e.getProps().get("timestamp"));
        
        String msg = obj.toJSONString();
        
        try {
            //String m = new String("{\"type\":\"vibration\",\"sensorId\":\"L1_ST10_R3_S2\",\"timestamp\":\"2014-10-09T13:07:41 UTC\"}");
	    System.out.println(msg);
            client.publish(TOPIC, msg.getBytes(), 0, false);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    private static class MyCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            System.out.println("Lost connection to the broker!");
        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            JSONParser parser = new JSONParser();
            Object obj = null;
            try {
                obj = parser.parse(new String(mqttMessage.getPayload()));
            } catch (ParseException ex) {
                Logger.getLogger(RuleEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
            }        
            JSONObject jsonObject = (JSONObject) obj;    
            
            String eventId = jsonObject.get("eventId").toString();
            String type = jsonObject.get("type").toString();
            String sensorId = jsonObject.get("sensorId").toString();
            String timestamp = jsonObject.get("timestamp").toString();
            
            RuleEngineImpl re = RuleEngineImpl.getInstance();
            for (Event e : re.getEvents()) {
                if (eventId.equals(e.getId())) {
                    e.getProps().put("status", "resolved");
                    PersonDynamicStore.getInstance()
                            .getPersonByErpId(e.getProps().get("responsibleId").toString()).getProps()
                            .put("available", true);
                }
            }
            
            String msg = "%%% " + eventId + " " + type + " " + sensorId + " " + timestamp;
            System.out.println(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            System.out.println("Message delivered to the broker");
        }
    }

}
