package com.madmax.emexws.ui.controllers;

import javafx.scene.Node;
import javafx.stage.Stage;

public class AbstractController implements Controller {
    private Node view;
    private Stage stage;

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node view) {
        this.view = view;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
