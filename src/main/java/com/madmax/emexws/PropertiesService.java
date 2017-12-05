package com.madmax.emexws;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.util.Properties;

@Component
public class PropertiesService {
    private final static Logger log = Logger.getLogger(PropertiesService.class);

    private Properties properties;

    @Autowired
    public PropertiesService(Properties properties) {
        this.properties = properties;
    }

    public void load() throws IOException {

        try {
            File propsFile = new File("emexws.properties");
            InputStream in = new FileInputStream(propsFile);
            DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
            persister.load(properties, in);
        }
        catch (FileNotFoundException e) {
            log.trace("Property file not found.");
        }
    }

    public void save() throws IOException {
        File propsFile = new File("emexws.properties");
        OutputStream out = new FileOutputStream(propsFile);
        DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
        persister.store(properties, out, "EMEXWS Properties");
    }
}
