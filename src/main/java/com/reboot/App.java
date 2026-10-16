package com.reboot;

import com.reboot.view.ExplorationView;
import com.reboot.view.ScaledScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        ScaledScreen screen = new ScaledScreen(ExplorationView.VIEW_WIDTH, ExplorationView.VIEW_HEIGHT);
        Scene scene = new Scene(screen);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setTitle("Reboot");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.show();
        new GameController(screen).start();
    }

    public static void main(String[] args) {
        launch();
    }

}
