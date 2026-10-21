package com.reboot.view;

import com.reboot.model.fighter.Robot;
import com.reboot.model.game.ChoiceOutcome;
import com.reboot.model.game.ChoiceResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * L'écran « Reprogrammer ou achever », affiché après une victoire contre un robot.
 *
 * <p>Tant que le joueur n'a pas choisi, le robot glitche entre son visage piraté (rouge)
 * et son visage d'origine (bleu). Le choix se fait au clavier ou à la souris, et l'option
 * sélectionnée affiche ses conséquences avant d'être validée.
 */
public class ChoiceView extends StackPane {

    private static final int WIDTH = ExplorationView.VIEW_WIDTH;
    private static final int HEIGHT = ExplorationView.VIEW_HEIGHT;
    private static final double GLITCH_SECONDS = 0.9;
    private static final double JITTER_SECONDS = 0.12;
    private static final double SPRITE_SIZE = 256;
    private static final double SPRITE_X = (WIDTH - SPRITE_SIZE) / 2;
    private static final double SPRITE_Y = 70;
    private static final double BUTTON_Y = 560;
    private static final double BUTTON_WIDTH = 300;
    private static final double BUTTON_HEIGHT = 56;
    private static final double[] BUTTON_X = {150, WIDTH - 150 - BUTTON_WIDTH};
    private static final int WRAP = 64;

    private static final Color BACKGROUND = Color.rgb(10, 14, 28);
    private static final Color TEXT = Color.rgb(235, 238, 250);
    private static final Color MUTED = Color.rgb(150, 158, 190);
    private static final Color HOSTILE = Color.rgb(230, 50, 60);
    private static final Color ALLY = Color.rgb(60, 170, 255);
    private static final Color CODE = Color.rgb(70, 230, 120);

    private final Robot robot;
    private final String memory;
    private final ChoiceResolver resolver;
    private final Runnable onDone;
    private final SpriteStore sprites = new SpriteStore();
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final Random jitter = new Random();
    private final String[] previews;

    private int selected;
    private double time;
    private long lastFrame;
    private ChoiceOutcome outcome;

    private final AnimationTimer loop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            time += lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000.0;
            lastFrame = now;
            render();
        }
    };

    /**
     * @param robot le robot vaincu, encore détourné
     * @param memory le souvenir du robot, tiré du {@link com.reboot.model.story.MemoryBook}
     * @param resolver applique le choix et décrit ses conséquences
     * @param onDone appelé quand le joueur quitte l'écran, après avoir vu les conséquences
     */
    public ChoiceView(Robot robot, String memory, ChoiceResolver resolver, Runnable onDone) {
        this.robot = robot;
        this.memory = memory;
        this.resolver = resolver;
        this.onDone = onDone;
        this.previews = new String[] {resolver.previewReprogram(robot), resolver.previewFinish(robot)};
        canvas.getGraphicsContext2D().setImageSmoothing(false);
        getChildren().add(canvas);
        setFocusTraversable(true);
        setOnKeyPressed(this::onKeyPressed);
        setOnMouseMoved(this::onMouseMoved);
        setOnMouseClicked(this::onMouseClicked);
    }

    /** Démarre l'animation et prend le focus du clavier. */
    public void start() {
        requestFocus();
        lastFrame = 0;
        loop.start();
    }

    private void onKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        if (outcome != null) {
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                loop.stop();
                onDone.run();
            }
            return;
        }
        switch (code) {
            case Q, LEFT, DIGIT1, NUMPAD1, AMPERSAND -> selected = 0;
            case D, RIGHT, DIGIT2, NUMPAD2 -> selected = 1;
            case ENTER, SPACE -> choose();
            default -> {
                if ("é".equals(event.getText())) {
                    selected = 1;
                }
            }
        }
    }

    private void onMouseMoved(MouseEvent event) {
        int button = buttonAt(event.getX(), event.getY());
        if (outcome == null && button >= 0) {
            selected = button;
        }
    }

    private void onMouseClicked(MouseEvent event) {
        if (outcome == null && buttonAt(event.getX(), event.getY()) >= 0) {
            selected = buttonAt(event.getX(), event.getY());
            choose();
        }
    }

    private static int buttonAt(double x, double y) {
        for (int i = 0; i < BUTTON_X.length; i++) {
            if (x >= BUTTON_X[i] && x <= BUTTON_X[i] + BUTTON_WIDTH && y >= BUTTON_Y && y <= BUTTON_Y + BUTTON_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private void choose() {
        outcome = selected == 0 ? resolver.reprogram(robot) : resolver.finish(robot);
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        if (outcome == null) {
            renderChoice(gc);
        } else {
            renderOutcome(gc);
        }
    }

    private void renderChoice(GraphicsContext gc) {
        boolean hostile = (int) (time / GLITCH_SECONDS) % 2 == 0;
        boolean glitching = time % GLITCH_SECONDS < JITTER_SECONDS;
        Color tint = hostile ? HOSTILE : ALLY;
        String pose = hostile ? "front_hostile_idle" : "front_ally_idle";
        double offset = glitching ? jitter.nextInt(25) - 12 : 0;

        gc.setEffect(new DropShadow(40, tint));
        sprites.draw(gc, "robots/" + robot.getSpriteId() + "/battle/" + pose + ".png",
                SPRITE_X + offset, SPRITE_Y, SPRITE_SIZE, tint);
        gc.setEffect(null);
        if (glitching) {
            drawGlitchBands(gc, tint);
        }

        gc.setFill(tint);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 26));
        gc.fillText(hostile ? "ÉLIMINER. CIBLE." : "...je voulais juste aider...", WIDTH / 2.0, 360);

        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 17));
        gc.fillText("Dans les yeux de " + robot.getName() + " défile un souvenir :", WIDTH / 2.0, 410);
        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", 19));
        List<String> lines = wrap(memory);
        for (int i = 0; i < lines.size(); i++) {
            gc.fillText(lines.get(i), WIDTH / 2.0, 445 + i * 28);
        }

        drawButton(gc, 0, "1  Reprogrammer", ALLY);
        drawButton(gc, 1, "2  Achever", HOSTILE);
        gc.setFill(selected == 0 ? ALLY : HOSTILE);
        gc.setFont(Font.font("Monospaced", 17));
        gc.fillText(previews[selected], WIDTH / 2.0, BUTTON_Y + BUTTON_HEIGHT + 40);
    }

    private void drawGlitchBands(GraphicsContext gc, Color tint) {
        gc.setFill(tint.deriveColor(0, 1, 1, 0.35));
        for (int i = 0; i < 4; i++) {
            double y = SPRITE_Y + jitter.nextDouble() * SPRITE_SIZE;
            gc.fillRect(SPRITE_X - 20 + jitter.nextInt(40), y, SPRITE_SIZE, 4 + jitter.nextInt(8));
        }
    }

    private void drawButton(GraphicsContext gc, int index, String label, Color color) {
        double x = BUTTON_X[index];
        boolean active = index == selected;
        gc.setFill(active ? color.deriveColor(0, 1, 1, 0.25) : BACKGROUND);
        gc.fillRect(x, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        gc.setStroke(active ? color : MUTED);
        gc.setLineWidth(active ? 3 : 1);
        gc.strokeRect(x, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        gc.setFill(active ? TEXT : MUTED);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 22));
        gc.fillText(label, x + BUTTON_WIDTH / 2, BUTTON_Y + BUTTON_HEIGHT / 2);
    }

    private void renderOutcome(GraphicsContext gc) {
        Color color = outcome.reprogrammed() ? CODE : HOSTILE;
        if (outcome.reprogrammed()) {
            drawCodeRain(gc);
            sprites.draw(gc, "robots/" + robot.getSpriteId() + "/battle/front_ally_idle.png",
                    SPRITE_X, SPRITE_Y, SPRITE_SIZE, ALLY);
        } else {
            sprites.draw(gc, "robots/" + robot.getSpriteId() + "/battle/ko.png",
                    SPRITE_X, SPRITE_Y, SPRITE_SIZE, HOSTILE);
        }
        gc.setFill(color);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 34));
        gc.fillText(outcome.reprogrammed() ? "REPROGRAMMÉ" : "ACHEVÉ", WIDTH / 2.0, 380);
        gc.setFont(Font.font("Monospaced", 19));
        for (int i = 0; i < outcome.lines().size(); i++) {
            boolean networkLine = !outcome.reprogrammed() && i == outcome.lines().size() - 1;
            gc.setFill(networkLine ? HOSTILE : TEXT);
            gc.fillText(outcome.lines().get(i), WIDTH / 2.0, 440 + i * 34);
        }
        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        gc.fillText("Entrée : continuer", WIDTH / 2.0, 640);
    }

    private void drawCodeRain(GraphicsContext gc) {
        gc.setFont(Font.font("Monospaced", 14));
        gc.setFill(CODE.deriveColor(0, 1, 1, 0.35));
        for (int column = 0; column < 24; column++) {
            double x = 20 + column * 40;
            double y = (time * 120 + column * 53) % HEIGHT;
            gc.fillText(column % 2 == 0 ? "reboot();" : "0x1F", x, y);
        }
    }

    private static List<String> wrap(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (line.length() > 0 && line.length() + word.length() + 1 > WRAP) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        lines.add(line.toString());
        return lines;
    }
}
