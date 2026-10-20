package com.reboot.view;

import com.reboot.model.combat.Combat;
import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.Encounter;
import com.reboot.model.combat.EncounterResult;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.skill.Skill;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CombatView extends StackPane {

    private static final int WIDTH = ExplorationView.VIEW_WIDTH;
    private static final int HEIGHT = ExplorationView.VIEW_HEIGHT;
    private static final double PANEL_TOP = 500;
    private static final double BAR_WIDTH = 260;
    private static final int LOG_LINES = 4;

    private static final Color BACKGROUND = Color.rgb(24, 28, 48);
    private static final Color FLOOR = Color.rgb(44, 52, 82);
    private static final Color PANEL = Color.rgb(10, 14, 28);
    private static final Color TEXT = Color.rgb(235, 238, 250);
    private static final Color MUTED = Color.rgb(150, 158, 190);
    private static final Color HIGHLIGHT = Color.rgb(60, 180, 255);

    private final Encounter encounter;
    private final Combat combat;
    private final Consumer<EncounterResult> onEnd;
    private final SpriteStore sprites = new SpriteStore();
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final List<String> log = new ArrayList<>();
    private int selected;

    public CombatView(Encounter encounter, Consumer<EncounterResult> onEnd) {
        this.encounter = encounter;
        this.combat = encounter.getCombat();
        this.onEnd = onEnd;
        this.canvas.getGraphicsContext2D().setImageSmoothing(false);
        getChildren().add(canvas);
        setFocusTraversable(true);
        setOnKeyPressed(this::onKeyPressed);
        log.add("Un " + combat.getEnemy().getName() + " détourné attaque !");
    }

    public void start() {
        requestFocus();
        render();
    }

    private void onKeyPressed(KeyEvent event) {
        List<Skill> skills = combat.getPlayer().getSkills();
        if (encounter.isFinished()) {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                onEnd.accept(encounter.getResult());
            }
            return;
        }
        int shortcut = shortcutOf(event);
        if (shortcut >= 0 && shortcut < skills.size()) {
            selected = shortcut;
            play(skills.get(selected));
        } else {
            switch (event.getCode()) {
                case Z, UP -> selected = (selected + skills.size() - 1) % skills.size();
                case S, DOWN -> selected = (selected + 1) % skills.size();
                case ENTER, SPACE -> play(skills.get(selected));
                default -> { }
            }
        }
        render();
    }

    private static int shortcutOf(KeyEvent event) {
        return switch (event.getCode()) {
            case DIGIT1, NUMPAD1, AMPERSAND -> 0;
            case DIGIT2, NUMPAD2 -> 1;
            case DIGIT3, NUMPAD3, QUOTEDBL -> 2;
            default -> switch (event.getText()) {
                case "1", "&" -> 0;
                case "2", "é" -> 1;
                case "3", "\"" -> 2;
                default -> -1;
            };
        };
    }

    private void play(Skill skill) {
        log.addAll(combat.useSkill(skill).log());
        if (encounter.isFinished()) {
            log.add(combat.getOutcome() == CombatOutcome.VICTORY
                    ? "Victoire ! Entrée pour continuer."
                    : "Défaite... Entrée pour rentrer à la Home.");
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(FLOOR);
        gc.fillOval(560, 250, 320, 70);
        gc.fillOval(60, 430, 380, 80);

        Combattant enemy = combat.getEnemy();
        Combattant ally = combat.getPlayer();
        sprites.draw(gc, sprite(enemy, enemy.isKo() ? "ko" : enemy.isReprogrammable() ? "front_hostile_idle" : "front_idle"), 624, 100, 192, Color.CRIMSON);
        sprites.draw(gc, sprite(ally, ally.isKo() ? "ko" : "back_idle"), 122, 230, 256, Color.DODGERBLUE);
        drawStatus(gc, enemy, 60, 60);
        drawStatus(gc, ally, 600, 380);

        gc.setFill(PANEL);
        gc.fillRect(0, PANEL_TOP, WIDTH, HEIGHT - PANEL_TOP);
        gc.setFont(Font.font("Monospaced", 18));
        int from = Math.max(0, log.size() - LOG_LINES);
        for (int i = from; i < log.size(); i++) {
            gc.setFill(i == log.size() - 1 ? TEXT : MUTED);
            gc.fillText(log.get(i), 30, PANEL_TOP + 40 + (i - from) * 28);
        }
        if (!encounter.isFinished()) {
            List<Skill> skills = ally.getSkills();
            for (int i = 0; i < skills.size(); i++) {
                gc.setFill(i == selected ? HIGHLIGHT : TEXT);
                gc.fillText((i == selected ? "> " : "  ") + (i + 1) + "  " + skills.get(i).getName(),
                        620, PANEL_TOP + 40 + i * 28);
            }
        }
    }

    private void drawStatus(GraphicsContext gc, Combattant fighter, double x, double y) {
        gc.setFill(PANEL);
        gc.fillRect(x, y, BAR_WIDTH + 40, 86);
        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
        gc.fillText(fighter.getName() + "  Nv " + fighter.getLevel(), x + 20, y + 30);
        double ratio = (double) fighter.getHp() / fighter.getMaxHp();
        gc.setFill(Color.rgb(50, 56, 80));
        gc.fillRect(x + 20, y + 42, BAR_WIDTH, 12);
        gc.setFill(ratio > 0.5 ? Color.LIMEGREEN : ratio > 0.2 ? Color.ORANGE : Color.CRIMSON);
        gc.fillRect(x + 20, y + 42, BAR_WIDTH * ratio, 12);
        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText(fighter.getHp() + " / " + fighter.getMaxHp() + " PV", x + 20, y + 74);
    }

    private static String sprite(Combattant fighter, String pose) {
        return "robots/" + fighter.getSpriteId() + "/battle/" + pose + ".png";
    }
}
