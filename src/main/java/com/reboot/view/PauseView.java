package com.reboot.view;

import com.reboot.model.settings.KeyBindings;
import java.util.List;

/** Le menu pause, ouvert pendant l'exploration. */
public class PauseView extends MenuView {

    private final Runnable onResume;
    private final Runnable onQuests;
    private final Runnable onOptions;
    private final Runnable onMainMenu;

    /**
     * @param keys les touches du joueur
     * @param onResume reprend la partie
     * @param onQuests ouvre l'onglet des quêtes
     * @param onOptions ouvre les options
     * @param onMainMenu abandonne la partie et revient au menu principal
     */
    public PauseView(KeyBindings keys, Runnable onResume, Runnable onQuests, Runnable onOptions,
                     Runnable onMainMenu) {
        super(keys);
        this.onResume = onResume;
        this.onQuests = onQuests;
        this.onOptions = onOptions;
        this.onMainMenu = onMainMenu;
    }

    @Override
    protected String title() {
        return "PAUSE";
    }

    @Override
    protected String subtitle() {
        return "La partie est en attente.";
    }

    @Override
    protected List<MenuItem> items() {
        return List.of(
                MenuItem.button("Reprendre", onResume),
                MenuItem.button("Quêtes", onQuests),
                MenuItem.button("Options", onOptions),
                MenuItem.button("Menu principal (abandonner la partie)", onMainMenu));
    }

    @Override
    protected void onBack() {
        onResume.run();
    }
}
