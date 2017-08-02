package com.madmax.emexws.ui.controllers;

import javafx.scene.control.Alert;

public class ConfirmMessageBox extends Alert{
    public ConfirmMessageBox(String message) {
        super(AlertType.CONFIRMATION);
        setTitle("Подтверждение");
        setHeaderText(null);
        setContentText(message);
    }
}
