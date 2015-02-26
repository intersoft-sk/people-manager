/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager;

import eu.ebbitsproject.peoplemanager.model.Event;
import eu.ebbitsproject.peoplemanager.model.Person;
import eu.ebbitsproject.peoplemanager.utils.MQTTProxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimedRuleExectionOption;

/**
 *
 * @author glukac
 */
public class RuleEngineImpl implements RuleEngine {
    
    private static volatile RuleEngineImpl instance = null;
    
    private List<Event> events = null;
    
    private KieServices ks; 
    private KieContainer kcontainer;
    private KieSession ksessionBusiness;
    private KieSession ksessionPM;

    private RuleEngineImpl() {
        this.events = new ArrayList<>();
        this.initKnowledgeBase();
        this.initDummyData();
    }        
    
    public static synchronized RuleEngineImpl getInstance() {
        if (instance == null) {
            instance = new RuleEngineImpl();
        }
        return instance;
    }
    
    @Override
    public void insertEvent(String eventJSON) {
        
        System.out.println("eventJSON print::: " + eventJSON);
        
        Event event = this.parseEventJSON(eventJSON);
        
        System.out.println("##### Event: " + event.toString());
        if (null != event.getType()) switch (event.getType()) {
            case "event:caw":
                this.ksessionPM.insert(event);
                this.ksessionPM.fireAllRules();
                this.events.add(event);
                break;
            case "vibration":
                this.ksessionBusiness.insert(event);
                this.ksessionBusiness.fireAllRules();
                this.events.add(event);
                break;
        }

    }

    @Override
    public void updateEvent(String eventJSON) {
        System.out.println("Update event: " + eventJSON);
        
        // Parsing 
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(eventJSON);
        } catch (ParseException ex) {
            Logger.getLogger(RuleEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject jsonObject = (JSONObject) obj;  
        
        String eventId = jsonObject.get("id").toString();
        String personId = jsonObject.get("responsibleId").toString();
        String name = jsonObject.get("responsibleName").toString();
        String status = jsonObject.get("status").toString();
        
        for (Event e : this.events) {
            if (eventId.equals(e.getId())) {
                e.getProps().put("responsibleId", personId);
                e.getProps().put("responsibleName", name);
                if ("new".equals(status)) {
                    e.getProps().put("status", "active");
                    if (personId.equals("3")) {
                        MQTTProxy.getInstance().sendMessage(e);
                    }
                    
                    Event availEvent = new Event();
                    availEvent.setType("VIBRATION_ERROR");
                    availEvent.getProps().put("responsible", personId);
                    this.ksessionPM.insert(availEvent);
                    this.ksessionPM.fireAllRules();
                    
                }
                else if ("active".equals(status)) {
                    e.getProps().put("status", "resolved");
                    System.out.println("Error resolved...");
                    PersonDynamicStore.getInstance().getPersonByErpId(personId).getProps().put("available", true);
//                    Event availEvent = new Event();
//                    availEvent.setType("VIBRATION_ERROR_RESOLVED");
//                    availEvent.getProps().put("responsible", personId);                    
//                    this.ksessionPM.insert(availEvent);
//                    this.ksessionPM.fireAllRules();
                }                                
            }
        }
    }        
    
    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    @Override
    public List<Person> getPersons() {
        return PersonDynamicStore.getInstance().getPersons();
    }

    @Override
    public Person getPersonById(String id) {
        return PersonDynamicStore.getInstance().getPersonById(id);
    }   

    @Override
    public Person getPersonByErpId(String erpId) {
        return PersonDynamicStore.getInstance().getPersonByErpId(erpId);
    }        

    @Override
    public boolean isPersonAvailableByRfid(String rfid) {
        Person person = PersonDynamicStore.getInstance().getPersonByRfid(rfid);
        if (person != null) {
            if (true == (boolean) (person.getProps().get("available"))) {
                return true;
            }
        }
        return false;
    }        
    
    @Override
    public boolean isPersonAvailableByErpId(String erpId) {
        Person person = PersonDynamicStore.getInstance().getPersonByErpId(erpId);
        if (person != null) {
            if (true == (boolean) (person.getProps().get("available"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPersonAvailableById(String id) {
        Person person = PersonDynamicStore.getInstance().getPersonById(id);
        if (person != null) {
            if (true == (boolean) (person.getProps().get("available"))) {
                return true;
            }
        }
        return false;
    }
    
    private void initKnowledgeBase() {
        this.ks = KieServices.Factory.get();
        this.kcontainer = this.ks.getKieClasspathContainer();
        KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
        ksconf.setOption( TimedRuleExectionOption.YES );
        this.ksessionBusiness = kcontainer.newKieSession("business-rules");
        this.ksessionPM = kcontainer.newKieSession("pm-rules", ksconf);
    }
    
    private void initDummyData() {
        Map<String, Object> pp1 = new HashMap<>();
        pp1.put("available", true);        
        pp1.put("erpId", "1");
        pp1.put("responsibility", "building1");
        Person person1 = new Person(pp1);
        
        Map<String, Object> pp2 = new HashMap<>();
        pp2.put("available", true);
        pp2.put("erpId", "2");
        pp2.put("responsibility", "building1");
        Person person2 = new Person(pp2);

	Map<String, Object> pp3 = new HashMap<>();
        pp3.put("available", true);
        pp3.put("erpId", "3");
        Person person3 = new Person(pp3);
        
        Map<String, Object> pp4 = new HashMap<>();
        pp4.put("available", true);
        pp4.put("erpId", "4");
        pp4.put("responsibility", "L1_ST3");
        pp4.put("role", 1);
        Person person4 = new Person(pp4);
        
        Map<String, Object> pp5 = new HashMap<>();
        pp5.put("available", true);
        pp5.put("erpId", "5");
        pp5.put("responsibility", "L1_ST3");
        pp5.put("role", 1);
        Person person5 = new Person(pp5);
        
        PersonDynamicStore erp = PersonDynamicStore.getInstance();
        erp.addPerson(person1);
        erp.addPerson(person2); 
	erp.addPerson(person3);        
        erp.addPerson(person4);    
        erp.addPerson(person5);    
    }
    
    private Event parseEventJSON(String eventJSON) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(eventJSON);
        } catch (ParseException ex) {
            Logger.getLogger(RuleEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject jsonObject = (JSONObject) obj;      
        Event event = null;
        
        System.out.println(jsonObject.get("type"));
        // --------------------------------------------------------------------------------
        if (jsonObject.get("type").equals("event:caw")) {
            Person person = null;
            if (jsonObject.get("erpId") != null) {
                person = PersonDynamicStore.getInstance().getPersonByErpId(jsonObject.get("erpId").toString());
            }
            if (person == null && jsonObject.get("personId") != null) {
                person = PersonDynamicStore.getInstance().getPersonByErpId(jsonObject.get("personId").toString());
            }

            Map<String, Object> ps = new HashMap<>();
            if (jsonObject.get("beefWeigh") != null) {
                ps.put("beefWeigh", jsonObject.get("beefWeigh").toString());
            }
            if (jsonObject.get("beefWeighTime") != null) {
                ps.put("beefWeighTime", jsonObject.get("beefWeighTime").toString());
            }
            if (person != null) {
                ps.put("person", person);
            }

            event = new Event(jsonObject.get("type").toString(), ps);
        } else if (jsonObject.get("type").equals("vibration")) {
            // Event format for vibration detection
            // {“type”:”vibration”,”sensorid”:”L1_ST10_R3_S2”,”timestamp”:” Oct 07 2014 16:30:00”}
            Map<String, Object> ps = new HashMap<>();
            ps.put("sensorid", jsonObject.get("sensorid"));
            ps.put("timestamp", jsonObject.get("timestamp"));
            ps.put("status", "new");
            
            event = new Event(jsonObject.get("type").toString(), ps);
        }
        
        return event;
    }

}
