/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager.model;

import java.util.Map;
import java.util.UUID;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author glukac
 */
@XmlType
public class Person {
    
    private String personId;
    private Map<String, Object> props;

    public Person() {
        this.personId = UUID.randomUUID().toString();
    }
    
    public Person(Map<String, Object> properties) {
        this.personId = UUID.randomUUID().toString();
        this.props = properties;
    }

    public Person(String personId, Map<String, Object> properties) {
        this.personId = personId;
        this.props = properties;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }    

    @Override
    public String toString() {
        return "Person ID:" + this.personId + ", properties: " + this.getProps().toString();
    }        
    
}
