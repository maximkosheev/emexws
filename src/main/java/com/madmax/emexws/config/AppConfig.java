package com.madmax.emexws.config;

import com.madmax.emexws.models.User;
import com.madmax.emexws.models.impl.UserImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.madmax.emexws.models", "com.madmax.emexws.ui"})
public class AppConfig {

    @Bean
    User user() {
        return new UserImpl();
    }
}
