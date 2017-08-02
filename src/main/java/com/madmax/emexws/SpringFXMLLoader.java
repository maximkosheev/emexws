package com.madmax.emexws;

import com.madmax.emexws.config.AppConfig;
import com.madmax.emexws.ui.controllers.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;

public class SpringFXMLLoader {
    private static final ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);

    public static Controller load(String url) {
        try (InputStream fxmlStream = SpringFXMLLoader.class.getResourceAsStream(url)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> clazz) {
                    return appContext.getBean(clazz);
                }
            });
            Node view = (Node)loader.load(fxmlStream);
            Controller controller = loader.getController();
            controller.setView(view);
            return controller;
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
