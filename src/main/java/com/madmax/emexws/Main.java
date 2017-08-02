package com.madmax.emexws;

import com.madmax.emexws.ui.controllers.ConfirmMessageBox;
import com.madmax.emexws.ui.controllers.LoginFrame;
import com.madmax.emexws.ui.controllers.MainFrame;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.log4j.Logger;

public class Main extends Application {
    private static Logger logger = Logger.getLogger(Main.class);

    public static final String APPLICATION_NAME = "АРМ менеджера EMEX.ru г.Озерск";

    @Override
    public void start(Stage primaryStage) throws Exception{
        logger.trace("Staring application");
        LoginFrame loginFrame = (LoginFrame) SpringFXMLLoader.load("ui/frmLogin.fxml");
        loginFrame.setStage(primaryStage);
        loginFrame.getStage().addEventHandler(LoginFrame.LOGIN_EVENT, (event) -> {
            MainFrame mainFrame = (MainFrame)SpringFXMLLoader.load("ui/frmMain.fxml");
            mainFrame.setStage(primaryStage);
            mainFrame.getStage().show();
        });
        loginFrame.getStage().show();
        primaryStage.setOnCloseRequest((event) -> {
            ConfirmMessageBox dlg = new ConfirmMessageBox("Завершить работу приложения?");
            if (dlg.showAndWait().get() == ButtonType.CANCEL)
                event.consume();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
