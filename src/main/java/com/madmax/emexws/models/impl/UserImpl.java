package com.madmax.emexws.models.impl;

import com.madmax.emexws.exceptions.ValidateException;
import com.madmax.emexws.models.User;

public class UserImpl implements User {
    private String login;
    private String password;

    public UserImpl() {
        login = "";
        password = "";
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) throws ValidateException {
        if (login != null && !login.isEmpty()) {
            this.login = login;
        }
        else {
            throw new ValidateException("Логин не может быть пустым");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
