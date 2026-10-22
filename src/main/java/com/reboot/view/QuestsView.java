package com.reboot.view;

import com.reboot.model.game.Game;
import com.reboot.model.quest.Quest;
import com.reboot.model.quest.QuestGroup;
import com.reboot.model.settings.KeyBindings;
import java.util.List;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/** Onglet Quêtes. Montre, chapitre par chapitre, ce qu'il reste à faire pour débloquer la zone suivante. */
public class QuestsView extends MenuView {

    private static final double LEFT = 150;
    private static final double RIGHT = WIDTH - 150;
    private static final double TOP = 238;
    private static final double LINE = 25;
    private static final Color DONE = Color.rgb(90, 210, 130);
    private static final Color LOCKED = Color.rgb(80, 86, 110);
    private static final Color GUARDIAN = Color.rgb(255, 200, 80);

    private final Game game;
    private final Runnable onClose;

    /**
     * Crée l'onglet des quêtes.
     *
     * @param keys les touches du joueur
     * @param game la partie dont les quêtes sont affichées
     * @param onClose ferme l'onglet
     */
    public QuestsView(KeyBindings keys, Game game, Runnable onClose) {
        super(keys);
        this.game = game;
        this.onClose = onClose;
    }

    @Override
    protected String title() {
        return "QUÊTES";
    }

    @Override
    protected String subtitle() {
        return game.getCampaign().current(game).map(chapter -> "En cours : " + chapter.getDescription())
                .orElse("Néo-Lyon est libérée.");
    }

    @Override
    protected List<MenuItem> items() {
        return List.of(MenuItem.button("Retour", onClose));
    }

    @Override
    protected void onBack() {
        onClose.run();
    }

    @Override
    protected double itemsTop() {
        return 612;
    }

    @Override
    protected String footer() {
        return "Achever renforce les robots rencontrés · reprogrammer les fait entrer dans l'équipe";
    }

    @Override
    protected void decorate(GraphicsContext gc) {
        gc.setTextBaseline(VPos.CENTER);
        double y = TOP;
        boolean reached = true;
        for (QuestGroup chapter : game.getCampaign().getChapters()) {
            boolean done = chapter.isDone(game);
            drawChapter(gc, chapter, y, done, reached);
            y += LINE + 4;
            for (int i = 0; i < chapter.getQuests().size(); i++) {
                boolean guardian = i == chapter.getQuests().size() - 1;
                drawQuest(gc, chapter, chapter.getQuests().get(i), guardian, y, reached);
                y += LINE;
            }
            y += 10;
            reached = reached && done;
        }
        gc.setTextAlign(TextAlignment.CENTER);
    }

    private void drawChapter(GraphicsContext gc, QuestGroup chapter, double y, boolean done, boolean reached) {
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        gc.setFill(done ? DONE : reached ? TEXT : LOCKED);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(chapter.getDescription(), LEFT, y);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(done ? "Terminé" : reached
                ? chapter.getProgress(game) + " / " + chapter.getTarget(game) : "Verrouillé", RIGHT, y);
    }

    private void drawQuest(GraphicsContext gc, QuestGroup chapter, Quest quest, boolean guardian, double y,
                           boolean reached) {
        boolean done = quest.isDone(game);
        boolean waiting = guardian && !done && !chapter.isGuardianOpen(game);
        gc.setFont(Font.font("Monospaced", 15));
        gc.setFill(!reached ? LOCKED : done ? DONE : waiting ? MUTED : guardian ? GUARDIAN : TEXT);
        gc.setTextAlign(TextAlignment.LEFT);
        String box = done ? "[x] " : "[ ] ";
        gc.fillText("   " + box + quest.getDescription() + (waiting ? " · verrouillé" : ""), LEFT, y);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(Math.min(quest.getProgress(game), quest.getTarget(game)) + " / " + quest.getTarget(game),
                RIGHT, y);
    }
}
