/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author glukac
 */
@XmlType
public class Event {
    
    private String id;
    private String type;
    private Map<String, Object> props;
    

    public Event() {
        this.id = UUID.randomUUID().toString();
        this.props = new HashMap<>();
    }

    public Event(String type) {
        this.type = type;
        this.id = UUID.randomUUID().toString();
        this.props = new HashMap<>();        
    }
    
    public Event(String type, Map<String, Object> properties) {
        this.type = type;
        this.id = UUID.randomUUID().toString();
        this.props = properties;
    }      

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }   

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }        

    @Override
    public String toString() {
        return "Event type: " + type + " with ID: " + id + ", properties: " 
                + props.toString();
    }        
    
}
