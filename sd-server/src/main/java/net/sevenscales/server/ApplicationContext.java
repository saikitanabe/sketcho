package net.sevenscales.server;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ApplicationContext {
	private GenericApplicationContext genericContext;
	private static ApplicationContext instance;
	
	static public Logger logger = Logger.getLogger(ApplicationContext.class);

	private ApplicationContext() {
	}
	
	public static ApplicationContext getInstance() {
		if (instance == null) {
			instance = new ApplicationContext();
		}		
		return instance;
	}

	public Object getBean(String beanId) {
		return genericContext.getBean(beanId);
	}
	
  public ApplicationContext loadConfiguration(
      Configuration configuration) {
    if (genericContext == null) {
      genericContext = new GenericApplicationContext();
      XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(genericContext);
      xmlReader.loadBeanDefinitions(new ClassPathResource
          (configuration.springConfigurationFile));
      genericContext.refresh();
      
      logger.setLevel(Level.DEBUG);
      logger.info("Application Context loaded: " + configuration.springConfigurationFile); 
    }
    return this;
  }

}
