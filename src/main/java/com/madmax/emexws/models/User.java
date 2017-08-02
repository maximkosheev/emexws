package com.madmax.emexws.models;

import com.madmax.emexws.exceptions.ValidateException;

public interface User {
    String getLogin();
    void setLogin(String login) throws ValidateException;
    String getPassword();
    void setPassword(String password) throws ValidateException;
}
