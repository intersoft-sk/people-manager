/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ebbitsproject.peoplemanager.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author glukac
 */
public class HttpUtils {
    
    public static void alert(String errorType, String objectId) {
        System.out.println("Alert sent without assignment.");
    }
    
    public static void alert(String errorType, String objectId, String personId, String eventId) {
	postData(PropertiesUtils.getProperty("uiapp.address"), "message", errorType, objectId, personId, eventId);
        System.out.println("Alert sent with assignment to person " + personId);
    }

    public static void postData(String url, String action, String errorType, String objectId, String personId, String eventId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("action", action));
        nvps.add(new BasicNameValuePair("personId", personId));
        nvps.add(new BasicNameValuePair("errorType", errorType));
        nvps.add(new BasicNameValuePair("objectId", objectId));
        nvps.add(new BasicNameValuePair("eventId", eventId));

	System.out.println("action = " + action);
	System.out.println("personId = " + personId);
	System.out.println("errorType = " + errorType);
	System.out.println("objectId = " + objectId);
	System.out.println("eventId = " + eventId);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        } catch (IOException e) {
            Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    //    public static String findLocation(String objectId) {
    //    return "building 1";
    //}
    
    //public static String findPerson(String errorType, String location) {
    //    return "johndoe";
    //}

    public static String findLocation(String objectId) {
        Object[] res = null;
        try {
            res = OMProxy.getClient().invoke("findLocation", objectId);
        } catch (Exception ex) {
            Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        String location = res[0].toString();
        return location;
    }

    public static String findPerson(String errorType, String location) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

	String url = PropertiesUtils.getProperty("uiapp.address");
	
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("action", "find-persons"));
        String properties = "demo-e1:competence=" + errorType + ",demo-e1:area-responsibility=" + location;
	String pmProperties = "available=true";
        nvps.add(new BasicNameValuePair("properties", properties));
        nvps.add(new BasicNameValuePair("pmProperties", pmProperties));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        CloseableHttpResponse response = null;
        String personJSON = null;
        try {
            response = httpClient.execute(httpPost);
            personJSON = EntityUtils.toString(response.getEntity());
	    System.out.println("######## PersonJSON: " + personJSON);
        } catch (IOException e) {
            Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, e);
            }
        }

	if (personJSON != null) {
	    JSONArray persons = (JSONArray) JSONValue.parse(personJSON);
	    Iterator<JSONObject> i = persons.iterator();
	    if(i.hasNext()){
		JSONObject o = i.next();
		return o.get("id").toString();
	    }	    
	}
	
	return personJSON;
   }

}
