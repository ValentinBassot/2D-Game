package com.reboot.view;

import com.reboot.model.settings.KeyBindings;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Le menu principal, affiché au lancement du jeu et après une partie. */
public class MainMenuView extends MenuView {

    private static final String[] ROBOTS = {"tankbot", "healerbot", "infighterbot"};

    private final SpriteStore sprites = new SpriteStore();
    private final Runnable onNewGame;
    private final Runnable onOptions;
    private final Runnable onQuit;

    /**
     * @param keys les touches du joueur
     * @param onNewGame lance une nouvelle partie
     * @param onOptions ouvre les options
     * @param onQuit quitte le jeu
     */
    public MainMenuView(KeyBindings keys, Runnable onNewGame, Runnable onOptions, Runnable onQuit) {
        super(keys);
        this.onNewGame = onNewGame;
        this.onOptions = onOptions;
        this.onQuit = onQuit;
    }

    @Override
    protected String title() {
        return "REBOOT";
    }

    @Override
    protected String subtitle() {
        return "Néo-Lyon. NEXUS a piraté le data center qui relie toutes les IA.";
    }

    @Override
    protected List<MenuItem> items() {
        return List.of(
                MenuItem.button("Nouvelle partie", onNewGame),
                MenuItem.button("Options", onOptions),
                MenuItem.button("Quitter", onQuit));
    }

    @Override
    protected String footer() {
        return "Entrée : valider  ·  ZQSD / flèches : naviguer";
    }

    @Override
    protected void decorate(GraphicsContext gc) {
        for (int i = 0; i < ROBOTS.length; i++) {
            sprites.draw(gc, "robots/" + ROBOTS[i] + "/battle/front_hostile_idle.png",
                    150 + i * 270, 470, 160, Color.rgb(40, 46, 70));
        }
    }
}
