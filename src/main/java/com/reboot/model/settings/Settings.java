package com.reboot.model.settings;

import java.util.List;
import java.util.Properties;

/**
 * Les options du joueur : taille de la fenêtre, plein écran, luminosité et touches.
 * Elles s'enregistrent sous forme de {@link Properties} pour être retrouvées au prochain lancement.
 */
public class Settings {

    /** Les tailles de fenêtre proposées, de la plus petite à la plus grande. */
    public static final List<WindowSize> WINDOW_SIZES = List.of(
            new WindowSize(960, 720), new WindowSize(1200, 900), new WindowSize(1440, 1080));
    /** La luminosité la plus basse. */
    public static final int MIN_BRIGHTNESS = -5;
    /** La luminosité la plus haute. */
    public static final int MAX_BRIGHTNESS = 5;

    private static final String WINDOW = "window.size";
    private static final String FULLSCREEN = "window.fullscreen";
    private static final String BRIGHTNESS = "display.brightness";
    private static final String KEY_PREFIX = "key.";

    private final KeyBindings keys = new KeyBindings();
    private int windowSizeIndex;
    private boolean fullscreen;
    private int brightness;

    /**
     * Passe à la taille de fenêtre suivante ou précédente, en boucle.
     *
     * @param delta {@code 1} pour la suivante, {@code -1} pour la précédente
     */
    public void cycleWindowSize(int delta) {
        windowSizeIndex = Math.floorMod(windowSizeIndex + delta, WINDOW_SIZES.size());
    }

    /**
     * Change la luminosité, sans sortir des bornes.
     *
     * @param delta le pas à ajouter, positif ou négatif
     */
    public void adjustBrightness(int delta) {
        brightness = Math.max(MIN_BRIGHTNESS, Math.min(MAX_BRIGHTNESS, brightness + delta));
    }

    /** @param fullscreen {@code true} pour jouer en plein écran */
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    /** @return la taille de fenêtre choisie */
    public WindowSize getWindowSize() { return WINDOW_SIZES.get(windowSizeIndex); }

    /** @return {@code true} si le jeu est en plein écran */
    public boolean isFullscreen() { return fullscreen; }

    /** @return la luminosité, entre {@value #MIN_BRIGHTNESS} et {@value #MAX_BRIGHTNESS} */
    public int getBrightness() { return brightness; }

    /** @return les touches du joueur */
    public KeyBindings getKeys() { return keys; }

    /** @return les options, prêtes à être enregistrées dans un fichier */
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.setProperty(WINDOW, Integer.toString(windowSizeIndex));
        properties.setProperty(FULLSCREEN, Boolean.toString(fullscreen));
        properties.setProperty(BRIGHTNESS, Integer.toString(brightness));
        for (Action action : Action.values()) {
            properties.setProperty(KEY_PREFIX + action.name(), keys.keyOf(action));
        }
        return properties;
    }

    /**
     * Relit des options enregistrées. Une valeur absente ou invalide garde sa valeur par défaut :
     * un fichier abîmé ne doit jamais empêcher le jeu de démarrer.
     *
     * @param properties les options lues dans le fichier
     * @return les options
     */
    public static Settings fromProperties(Properties properties) {
        Settings settings = new Settings();
        settings.windowSizeIndex = readInt(properties, WINDOW, 0, 0, WINDOW_SIZES.size() - 1);
        settings.fullscreen = Boolean.parseBoolean(properties.getProperty(FULLSCREEN, "false"));
        settings.brightness = readInt(properties, BRIGHTNESS, 0, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        for (Action action : Action.values()) {
            String key = properties.getProperty(KEY_PREFIX + action.name());
            if (key != null && !key.isBlank()) {
                settings.keys.bind(action, key);
            }
        }
        return settings;
    }

    private static int readInt(Properties properties, String name, int fallback, int min, int max) {
        try {
            int value = Integer.parseInt(properties.getProperty(name, Integer.toString(fallback)).trim());
            return value < min || value > max ? fallback : value;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
