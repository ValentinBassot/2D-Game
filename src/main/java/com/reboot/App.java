package com.reboot;

import com.reboot.model.settings.Settings;
import com.reboot.model.settings.WindowSize;
import com.reboot.view.ExplorationView;
import com.reboot.view.ScaledScreen;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX. Ouvre la fenêtre, charge les options du joueur et les réapplique dès qu'elles
 * changent.
 */
public class App extends Application {

    private static final Path SETTINGS_FILE = Path.of(System.getProperty("user.home"), ".reboot", "settings.properties");

    private Stage stage;
    private ScaledScreen screen;
    private Settings settings;

    /** Constructeur appelé par JavaFX au lancement, on ne l'utilise pas directement. */
    public App() {
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        settings = loadSettings();
        screen = new ScaledScreen(ExplorationView.VIEW_WIDTH, ExplorationView.VIEW_HEIGHT);
        Scene scene = new Scene(screen);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                settings.setFullscreen(!stage.isFullScreen());
                applySettings();
                event.consume();
            }
        });
        stage.setTitle("Reboot");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.fullScreenProperty().addListener((obs, was, now) -> settings.setFullscreen(now));
        applySettings();
        stage.show();
        new GameController(screen, settings, this::applySettings).start();
    }

    private void applySettings() {
        WindowSize size = settings.getWindowSize();
        if (settings.isFullscreen()) {
            stage.setFullScreen(true);
        } else {
            stage.setFullScreen(false);
            screen.setPrefSize(size.width(), size.height());
            stage.sizeToScene();
            stage.centerOnScreen();
        }
        screen.setBrightness(settings.getBrightness());
        saveSettings();
    }

    private static Settings loadSettings() {
        Properties properties = new Properties();
        if (Files.exists(SETTINGS_FILE)) {
            try (Reader reader = Files.newBufferedReader(SETTINGS_FILE, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException e) {
                System.err.println("Options illisibles, valeurs par défaut utilisées : " + e.getMessage());
            }
        }
        return Settings.fromProperties(properties);
    }

    private void saveSettings() {
        try {
            Files.createDirectories(SETTINGS_FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(SETTINGS_FILE, StandardCharsets.UTF_8)) {
                settings.toProperties().store(writer, "Options de Reboot");
            }
        } catch (IOException e) {
            System.err.println("Options non enregistrées : " + e.getMessage());
        }
    }

    /**
     * Lance le jeu.
     *
     * @param args les arguments de la ligne de commande, non utilisés
     */
    public static void main(String[] args) {
        launch();
    }

}
