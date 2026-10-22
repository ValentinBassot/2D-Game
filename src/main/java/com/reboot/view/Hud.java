package com.reboot.view;

import com.reboot.model.fighter.Robot;
import com.reboot.model.game.Game;
import com.reboot.model.nexus.DataCenter;
import com.reboot.model.nexus.WantedBar;
import com.reboot.model.nexus.WantedTier;
import java.util.List;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * HUD de l'exploration. En haut à gauche la zone, la barre de recherche, les pièces et le réseau, en bas
 * l'équipe et le rappel des touches.
 *
 * <p>Il lit seulement l'état de la partie, il ne modifie rien.
 */
public class Hud {

    private static final double MARGIN = 12;
    private static final double PANEL_WIDTH = 320;
    private static final double PANEL_HEIGHT = 160;
    private static final double BAR_WIDTH = 190;
    private static final double CARD_WIDTH = 296;
    private static final double CARD_HEIGHT = 70;
    private static final double HINT_HEIGHT = 26;
    private static final double ICON = 22;

    private static final Color FRAME = Color.rgb(10, 14, 28, 0.82);
    private static final Color TEXT = Color.rgb(235, 238, 250);
    private static final Color MUTED = Color.rgb(150, 158, 190);
    private static final Color TRACK = Color.rgb(50, 56, 80);
    private static final Color REPAIR = Color.rgb(255, 170, 60);
    private static final Color QUEST = Color.rgb(255, 200, 80);

    private final Game game;
    private final SpriteStore sprites = new SpriteStore();

    /**
     * Crée le HUD d'une partie.
     *
     * @param game la partie dont l'état est affiché
     */
    public Hud(Game game) {
        this.game = game;
    }

    /**
     * Dessine le HUD par-dessus la carte.
     *
     * @param gc le contexte graphique de la vue
     * @param zoneName le nom de la zone affichée
     * @param width la largeur de la vue
     * @param height la hauteur de la vue
     */
    public void draw(GraphicsContext gc, String zoneName, double width, double height) {
        gc.save();
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        drawStatusPanel(gc, zoneName);
        drawTeam(gc, height - MARGIN - HINT_HEIGHT - CARD_HEIGHT);
        drawHint(gc, width, height);
        gc.restore();
    }

    private void drawStatusPanel(GraphicsContext gc, String zoneName) {
        double x = MARGIN;
        double y = MARGIN;
        gc.setFill(FRAME);
        gc.fillRect(x, y, PANEL_WIDTH, PANEL_HEIGHT);

        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
        gc.fillText(zoneName, x + 14, y + 22);

        WantedBar wanted = game.getWantedBar();
        WantedTier tier = wanted.getTier();
        gc.setFont(Font.font("Monospaced", 14));
        gc.setFill(MUTED);
        gc.fillText("Recherche", x + 14, y + 52);
        double barX = x + 110;
        gc.setFill(TRACK);
        gc.fillRect(barX, y + 46, BAR_WIDTH, 12);
        gc.setFill(colorOf(tier));
        gc.fillRect(barX, y + 46, BAR_WIDTH * wanted.getValue() / WantedBar.MAX, 12);
        gc.setFill(colorOf(tier));
        gc.fillText(wanted.getValue() + " · " + tier.getLabel(), barX, y + 72);

        sprites.draw(gc, "ui/piece.png", x + 12, y + 88, ICON, Color.GOLD);
        gc.setFill(TEXT);
        gc.fillText("x " + game.getParts().getParts(), x + 40, y + 99);
        gc.setFill(MUTED);
        gc.fillText("Home : " + game.getHome().getStored().size() + " robot(s)", x + 110, y + 99);

        DataCenter dataCenter = game.getDataCenter();
        gc.fillText("Réseau NEXUS : " + dataCenter.getRobotsKilled() + " achevé(s) · + "
                + dataCenter.getNetworkBonus(), x + 14, y + 124);

        gc.setFill(QUEST);
        gc.fillText(game.getCampaign().current(game)
                .map(chapter -> "Quêtes : " + chapter.getProgress(game) + " / " + chapter.getTarget(game)
                        + " (Échap > Quêtes)")
                .orElse("Quêtes terminées"), x + 14, y + 147);
    }

    private void drawTeam(GraphicsContext gc, double y) {
        List<Robot> robots = game.getTeam().getRobots();
        for (int i = 0; i < robots.size(); i++) {
            drawCard(gc, robots.get(i), MARGIN + i * (CARD_WIDTH + 8), y);
        }
    }

    private void drawCard(GraphicsContext gc, Robot robot, double x, double y) {
        gc.setFill(FRAME);
        gc.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        sprites.draw(gc, "robots/" + robot.getSpriteId() + "/battle/front_ally_idle.png",
                x + 6, y + 7, 56, Color.DODGERBLUE);

        double textX = x + 70;
        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        gc.fillText(robot.getName() + "  Nv " + robot.getLevel(), textX, y + 16);

        double barWidth = CARD_WIDTH - 82;
        double ratio = (double) robot.getHp() / robot.getMaxHp();
        gc.setFill(TRACK);
        gc.fillRect(textX, y + 30, barWidth, 9);
        gc.setFill(ratio > 0.5 ? Color.LIMEGREEN : ratio > 0.2 ? Color.ORANGE : Color.CRIMSON);
        gc.fillRect(textX, y + 30, barWidth * ratio, 9);

        gc.setFont(Font.font("Monospaced", 13));
        if (robot.isUnderRepair()) {
            gc.setFill(REPAIR);
            gc.fillText("Réparation : " + robot.getRepairVictoriesLeft() + " victoire(s)", textX, y + 54);
        } else {
            gc.setFill(MUTED);
            gc.fillText(robot.getHp() + " / " + robot.getMaxHp() + " PV", textX, y + 54);
        }
    }

    private static void drawHint(GraphicsContext gc, double width, double height) {
        gc.setFill(FRAME);
        gc.fillRect(0, height - HINT_HEIGHT, width, HINT_HEIGHT);
        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ZQSD / flèches : se déplacer  ·  cases de rencontre : combats  ·  F11 : plein écran",
                width / 2, height - HINT_HEIGHT / 2);
    }

    private static Color colorOf(WantedTier tier) {
        return switch (tier) {
            case UNKNOWN -> Color.rgb(90, 210, 130);
            case SPOTTED -> Color.rgb(240, 210, 80);
            case HUNTED -> Color.rgb(255, 140, 50);
            case PUBLIC_ENEMY -> Color.rgb(235, 55, 65);
        };
    }
}
