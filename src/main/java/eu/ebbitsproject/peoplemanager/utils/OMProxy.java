/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ebbitsproject.peoplemanager.utils;

import java.util.*;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class OMProxy {

    public static Client client = null;

    public static Set<String> getLocations(String sensorid) {
        try{
            Object[] res = OMProxy.getClient().invoke("findSensorLocations", sensorid);
            if(res != null && res[0] != null){
                return new HashSet(Arrays.asList(res[0].toString().split(",")));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static Client getClient() {
        try {
            if (client == null) {
                JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
                client = (Client) dcf.createClient(PropertiesUtils.getProperty("omproxy.address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    public static void main(String[] as) throws Exception {
        Object[] res = OMProxy.getClient().invoke("findLocation", "demo-e1:robot_A_1_1");
        String location = res[0].toString();
        System.out.println("LOCATION:: " + location);

    }
}
