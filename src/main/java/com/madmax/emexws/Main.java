package com.madmax.emexws;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String APPLICATION_NAME = "АРМ менеджера EMEX.ru г.Озерск";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("ui/frmMain.fxml"));
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setScene(new Scene(root, 1100, 768));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
