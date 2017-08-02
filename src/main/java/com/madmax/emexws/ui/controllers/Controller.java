package com.madmax.emexws.ui.controllers;

import javafx.scene.Node;
import javafx.stage.Stage;

public interface Controller {
    Node getView();
    void setView(Node view);
    Stage getStage();
    void setStage(Stage stage);
}
