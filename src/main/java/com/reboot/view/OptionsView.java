package com.reboot.view;

import com.reboot.model.settings.Action;
import com.reboot.model.settings.Settings;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Les options : taille de la fenêtre, plein écran, luminosité et touches.
 * Chaque changement est appliqué tout de suite, sans redémarrer le jeu.
 */
public class OptionsView extends MenuView {

    private final Settings settings;
    private final Runnable onChange;
    private final Runnable onClose;
    private Action waitingFor;

    /**
     * @param settings les options à modifier
     * @param onChange appelé après chaque changement, pour l'appliquer et l'enregistrer
     * @param onClose ferme les options
     */
    public OptionsView(Settings settings, Runnable onChange, Runnable onClose) {
        super(settings.getKeys());
        this.settings = settings;
        this.onChange = onChange;
        this.onClose = onClose;
    }

    @Override
    protected String title() {
        return "OPTIONS";
    }

    @Override
    protected String subtitle() {
        return waitingFor == null
                ? "Les changements s'appliquent tout de suite."
                : "Appuie sur la nouvelle touche pour « " + waitingFor.getLabel() + " »  (Échap : annuler)";
    }

    @Override
    protected List<MenuItem> items() {
        List<MenuItem> items = new ArrayList<>();
        items.add(MenuItem.setting("Taille de fenêtre : < " + settings.getWindowSize().label() + " >", delta -> {
            settings.cycleWindowSize(delta);
            onChange.run();
        }));
        items.add(MenuItem.setting("Plein écran : " + (settings.isFullscreen() ? "Oui" : "Non"), delta -> {
            settings.setFullscreen(!settings.isFullscreen());
            onChange.run();
        }));
        items.add(MenuItem.setting("Luminosité : < " + signed(settings.getBrightness()) + " >", delta -> {
            settings.adjustBrightness(delta);
            onChange.run();
        }));
        for (Action action : Action.values()) {
            items.add(MenuItem.button(action.getLabel() + " : " + display(settings.getKeys().keyOf(action)),
                    () -> waitingFor = action));
        }
        items.add(MenuItem.button("Touches par défaut", () -> {
            settings.getKeys().resetToDefaults();
            onChange.run();
        }));
        items.add(MenuItem.button("Retour", onClose));
        return items;
    }

    @Override
    protected boolean interceptKey(KeyEvent event) {
        if (waitingFor == null) {
            return false;
        }
        if (event.getCode() != KeyCode.ESCAPE) {
            settings.getKeys().bind(waitingFor, event.getCode().name());
            onChange.run();
        }
        waitingFor = null;
        return true;
    }

    @Override
    protected void onBack() {
        onClose.run();
    }

    @Override
    protected double itemsTop() {
        return 235;
    }

    @Override
    protected double itemHeight() {
        return 37;
    }

    @Override
    protected String footer() {
        return "Entrée : modifier  ·  Gauche / Droite : régler  ·  Échap : retour";
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }

    private static String display(String key) {
        try {
            return KeyCode.valueOf(key).getName();
        } catch (IllegalArgumentException e) {
            return key;
        }
    }
}
