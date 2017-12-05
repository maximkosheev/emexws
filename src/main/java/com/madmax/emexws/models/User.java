package com.madmax.emexws.models;

import com.madmax.emexws.exceptions.ValidateException;
import com.madmax.emexws.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class User {
    public static final String LOGIN_PROPERTY_NAME = "user.login";
    public static final String PASSWORD_PROPERTY_NAME = "user.password";

    private Properties properties;

    @Autowired
    public User(Properties properties) {
        this.properties = properties;
    }

    public Long getLogin() {
        try {
            return Long.parseLong(properties.getProperty(LOGIN_PROPERTY_NAME));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    public void setLogin(Long login) throws ValidateException {

        if (login == 0L)
            throw new ValidateException("Логин не может быть пустым");

        properties.setProperty(LOGIN_PROPERTY_NAME, login.toString());
    }

    public String getPassword() {
        return properties.getProperty(PASSWORD_PROPERTY_NAME);
    }

    public void setPassword(String password) {
        properties.setProperty(PASSWORD_PROPERTY_NAME, password);
    }
}
