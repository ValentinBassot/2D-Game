package com.reboot.view;

import com.reboot.model.fighter.Robot;
import com.reboot.model.game.GameStatus;
import com.reboot.model.settings.Action;
import com.reboot.model.settings.KeyBindings;
import com.reboot.model.team.Team;
import java.util.List;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class EndView extends StackPane {

    private static final int WIDTH = ExplorationView.VIEW_WIDTH;
    private static final int HEIGHT = ExplorationView.VIEW_HEIGHT;

    private static final Color BACKGROUND = Color.rgb(10, 14, 28);
    private static final Color TEXT = Color.rgb(235, 238, 250);
    private static final Color MUTED = Color.rgb(150, 158, 190);
    private static final Color VICTORY = Color.rgb(60, 220, 140);
    private static final Color DEFEAT = Color.rgb(230, 60, 70);

    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);

    public EndView(GameStatus status, Team team, Runnable onMainMenu, KeyBindings keys) {
        if (status == GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("The game is not over");
        }
        getChildren().add(canvas);
        setFocusTraversable(true);
        setOnKeyPressed(event -> {
            if (keys.actionOf(event.getCode().name()).orElse(null) == Action.CONFIRM) {
                onMainMenu.run();
            }
        });
        render(status == GameStatus.WON, team.getRobots());
    }

    public void start() {
        requestFocus();
    }

    private void render(boolean won, List<Robot> robots) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(won ? VICTORY : DEFEAT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 64));
        gc.fillText(won ? "VICTOIRE" : "GAME OVER", WIDTH / 2.0, 170);

        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", 22));
        gc.fillText(won
                ? "Le chef de NEXUS est vaincu : les robots retrouvent leur programmation."
                : "Tous tes robots sont en réparation : NEXUS garde le contrôle.", WIDTH / 2.0, 260);

        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("Ton équipe", WIDTH / 2.0, 340);
        for (int i = 0; i < robots.size(); i++) {
            Robot robot = robots.get(i);
            String status = robot.isUnderRepair() ? "en réparation" : robot.getHp() + " / " + robot.getMaxHp() + " PV";
            gc.setFill(TEXT);
            gc.fillText(robot.getName() + "  Nv " + robot.getLevel() + "  " + status, WIDTH / 2.0, 380 + i * 32);
        }

        gc.setFill(won ? VICTORY : DEFEAT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
        gc.fillText("Entrée : menu principal", WIDTH / 2.0, 600);
    }
}
