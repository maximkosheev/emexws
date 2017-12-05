package com.madmax.emexws.config;

import com.madmax.emexws.PropertiesService;
import com.madmax.emexws.models.User;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ComponentScan({"com.madmax.emexws"})
public class AppConfig {

    public static final String LAST_INVOICE_PATH_PROPERTY_NAME = "default_invoice_path";

    private final static Logger log = Logger.getLogger(AppConfig.class);

    @Bean
    public Properties properties() {
        Properties properties = new Properties();

        // устанавливаем значения свойств по умолчанию
        properties.setProperty(User.LOGIN_PROPERTY_NAME, "");
        properties.setProperty(User.PASSWORD_PROPERTY_NAME, "");
        properties.setProperty(LAST_INVOICE_PATH_PROPERTY_NAME, System.getProperty("user.home"));
        return properties;
    }

    @Bean
    public PropertiesService propertiesService() {
        return new PropertiesService(properties());
    }

    @PostConstruct
    public void load() {
        try {
            log.trace("Loading application properties");
            propertiesService().load();
        }
        catch (IOException e) {
            log.trace("Properties loading failed. Using default values.");
        }
    }

    @PreDestroy
    public void save() {
        try {
            log.trace("Saving application properties");
            propertiesService().save();
        }
        catch (IOException e) {
            log.trace("Properties saving failed.");
        }
    }
}
