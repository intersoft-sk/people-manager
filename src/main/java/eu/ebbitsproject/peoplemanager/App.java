package eu.ebbitsproject.peoplemanager;

import eu.ebbitsproject.peoplemanager.utils.MQTTProxy;
import eu.ebbitsproject.peoplemanager.utils.PropertiesUtils;
import org.apache.cxf.frontend.ServerFactoryBean;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {       
        
        MQTTProxy proxy = MQTTProxy.getInstance();
        
        ServerFactoryBean factory = new ServerFactoryBean();
        factory.setServiceClass(RuleEngine.class);
        factory.setServiceBean(RuleEngineImpl.getInstance());
        factory.setAddress(PropertiesUtils.getProperty("ruleengine.address"));
        factory.create();
        
    }
}
