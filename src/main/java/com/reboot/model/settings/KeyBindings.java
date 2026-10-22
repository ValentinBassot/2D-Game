package com.reboot.model.settings;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Touches du joueur. Chaque action a une touche principale qu'on peut changer dans les options, et les
 * flèches, Espace et Échap marchent toujours en secours.
 *
 * <p>On stocke les touches par leur nom ({@code "Z"}, {@code "UP"}, comme dans {@code
 * javafx.scene.input.KeyCode}). Comme ça le modèle ne dépend pas de JavaFX et se teste facilement.
 */
public class KeyBindings {

    private static final Map<Action, String> DEFAULTS = Map.of(
            Action.MOVE_UP, "Z",
            Action.MOVE_DOWN, "S",
            Action.MOVE_LEFT, "Q",
            Action.MOVE_RIGHT, "D",
            Action.CONFIRM, "ENTER",
            Action.PAUSE, "ESCAPE");

    private static final Map<String, Action> ALTERNATES = Map.of(
            "UP", Action.MOVE_UP,
            "DOWN", Action.MOVE_DOWN,
            "LEFT", Action.MOVE_LEFT,
            "RIGHT", Action.MOVE_RIGHT,
            "SPACE", Action.CONFIRM,
            "ESCAPE", Action.PAUSE);

    private final Map<Action, String> keys = new EnumMap<>(DEFAULTS);

    /** Crée les touches par défaut (ZQSD, Entrée et Échap). */
    public KeyBindings() {
    }

    /**
     * {@return la touche principale de cette action}
     *
     * @param action une action
     */
    public String keyOf(Action action) {
        return keys.get(action);
    }

    /**
     * Cherche l'action liée à une touche. Les touches principales sont prioritaires sur celles de secours.
     *
     * @param key le nom de la touche pressée
     * @return l'action liée, ou rien si la touche n'est liée à aucune action
     */
    public Optional<Action> actionOf(String key) {
        for (Map.Entry<Action, String> entry : keys.entrySet()) {
            if (entry.getValue().equals(key)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.ofNullable(ALTERNATES.get(key));
    }

    /**
     * Associe une nouvelle touche à une action. Si la touche était déjà prise, les deux actions échangent
     * leurs touches pour qu'aucune ne se retrouve sans touche.
     *
     * @param action l'action à modifier
     * @param key le nom de la nouvelle touche
     */
    public void bind(Action action, String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("A key is required");
        }
        String previous = keys.get(action);
        for (Map.Entry<Action, String> entry : keys.entrySet()) {
            if (entry.getKey() != action && entry.getValue().equals(key)) {
                entry.setValue(previous);
            }
        }
        keys.put(action, key);
    }

    /** Remet toutes les touches par défaut. */
    public void resetToDefaults() {
        keys.putAll(DEFAULTS);
    }
}
