/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.ebbitsproject.peoplemanager;

import eu.ebbitsproject.peoplemanager.model.Event;
import eu.ebbitsproject.peoplemanager.model.Person;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author glukac
 */
@WebService(name = "ruleengine", targetNamespace = "http://peoplemanager.ebbitsproject.eu")
public interface RuleEngine {
    
    @WebMethod
    void insertEvent(@WebParam(name = "eventJSON") String eventJSON);   
    
    @WebMethod
    void updateEvent(@WebParam(name = "eventJSON") String eventJSON);
    
    @WebMethod
    List<Event> getEvents();
    
    @WebMethod
    List<Person> getPersons();
    
    @WebMethod
    Person getPersonById(@WebParam(name = "id") String id);
    
    @WebMethod
    Person getPersonByErpId(@WebParam(name = "erpId") String erpId);
    
    @WebMethod
    boolean isPersonAvailableByRfid(@WebParam(name = "rfid") String rfid);
    
    @WebMethod
    boolean isPersonAvailableByErpId(@WebParam(name = "erpId") String erpId);
					
    @WebMethod
    boolean isPersonAvailableById(@WebParam(name = "id") String id);
    
}
