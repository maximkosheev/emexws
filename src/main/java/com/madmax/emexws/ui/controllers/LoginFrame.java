package com.madmax.emexws.ui.controllers;

import com.madmax.emexws.Main;
import com.madmax.emexws.models.User;
import com.madmax.emexws.ui.CriticalMessageBox;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginFrame extends AbstractController implements Initializable {
    @FXML TextField edtLogin;
    @FXML TextField edtPassword;
    @FXML Button btnLogin;

    public class LoginEvent extends Event {

        public LoginEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }

        public LoginEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
            super(source, target, eventType);
        }
    }

    public static final EventType<LoginEvent> LOGIN_EVENT = new EventType<>(Event.ANY, "LOGIN");

    @Autowired
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        getStage().setScene(new Scene((Parent)getView(),455, 320));
        getStage().setTitle(Main.APPLICATION_NAME);
    }

    @FXML
    void onLoginHandle(ActionEvent e) {
        try {
            user.setLogin(edtLogin.getText());
            user.setPassword(edtPassword.getText());
            LoginEvent loginEvent = new LoginEvent(LOGIN_EVENT);
            Event.fireEvent(getStage(), loginEvent);
        }
        catch (Exception error) {
            CriticalMessageBox alert = new CriticalMessageBox(error.getMessage());
            alert.showAndWait();
        }
    }
}
