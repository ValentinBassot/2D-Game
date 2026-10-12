package com.reboot;

import com.reboot.model.Player;
import com.reboot.model.TileMap;
import com.reboot.view.ExplorationView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        TileMap home = TileMap.load("/com/reboot/maps/home.txt");
        Player player = new Player(home.getSpawnX(), home.getSpawnY());
        ExplorationView exploration = new ExplorationView(home, player, "home");
        scene = new Scene(exploration);
        stage.setTitle("Reboot");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        exploration.start();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}