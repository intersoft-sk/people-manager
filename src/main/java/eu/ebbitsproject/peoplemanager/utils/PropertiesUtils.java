/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager.utils;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author glukac
 */
public class PropertiesUtils {

    private static Properties properties;
    
    static {
        PropertiesUtils utils =  new PropertiesUtils();
        try {
            properties = utils.getPropertiesFromClasspath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private PropertiesUtils() { }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    private Properties getPropertiesFromClasspath() 
            throws IOException {
        Properties props = new Properties();
        props.load(PropertiesUtils.class.getResourceAsStream("/config.properties"));
        return props;
    }
    
}
