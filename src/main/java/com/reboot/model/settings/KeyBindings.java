package com.reboot.model.settings;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Les touches du joueur. Chaque action a une touche principale, réassignable dans les options ;
 * les flèches, Espace et Échap restent des touches de secours qui marchent toujours.
 *
 * <p>Les touches sont des noms (ceux de {@code javafx.scene.input.KeyCode}, comme {@code "Z"}
 * ou {@code "UP"}) : le modèle ne dépend pas de JavaFX et se teste sans interface.
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

    /**
     * @param action une action
     * @return la touche principale de cette action
     */
    public String keyOf(Action action) {
        return keys.get(action);
    }

    /**
     * Trouve l'action déclenchée par une touche : les touches principales passent avant
     * les touches de secours.
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
     * Lie une action à une nouvelle touche. Si la touche servait déjà à une autre action,
     * les deux actions échangent leurs touches : aucune action ne reste sans touche.
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
