/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager;

import eu.ebbitsproject.peoplemanager.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author glukac
 */
public class PersonDynamicStore {
    
    private static volatile PersonDynamicStore instance = null;
    
    private List<Person> persons;
    
    private PersonDynamicStore() {
        this.persons = new ArrayList<>();
    } 
    
    public static synchronized PersonDynamicStore getInstance() {
        if (instance == null) {
            instance = new PersonDynamicStore();
        }
        return instance;
    }

    public List<Person> getPersons() {
        return persons;
    }        
    
    public void addPerson(Person person) {
        this.persons.add(person);
    }
    
    public Person getPersonByErpId(String erpId) {
        for (Person p : this.persons) {
            if (p.getProps().get("erpId").equals(erpId)) {
                return p;
            }
        }
        return null;
    }
    
    public Person getPersonByRfid(String rfid) {
        for (Person p : this.persons) {
            if (p.getProps().get("rfid").equals(rfid)) {
                return p;
            }
        }
        return null;
    }

    public Person getPersonById(String id) {
        for (Person p : this.persons) {
            if (p.getPersonId().equals(id)) {
                return p;
            }           
        }
        return null;
    }
    
    public List<Person> getPersonsByLocations(Set<String> locations) {
        List<Person> ps = new ArrayList<>();
        for (Person p : this.persons) {
            System.out.println("$$ Locations size: " + locations.size());
            System.out.println("$$ props: " + p.getProps());
            if (locations.contains(p.getProps().get("responsibility").toString())) {
                ps.add(p);
            }
        }
        return ps;
    }
    
}
