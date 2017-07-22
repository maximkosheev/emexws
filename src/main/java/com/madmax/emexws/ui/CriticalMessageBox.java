package com.madmax.emexws.ui;

import com.madmax.emexws.Main;
import javafx.scene.control.Alert;

public class CriticalMessageBox extends Alert{
    public CriticalMessageBox(String message) {
        super(AlertType.ERROR);
        setTitle("Ошибка");
        setHeaderText("В процессе работы возникла исключительная ситуация");
        setContentText(message);
    }
}
