package com.reboot.view;

import com.reboot.model.world.Direction;
import com.reboot.model.world.Player;
import com.reboot.model.world.Tile;
import com.reboot.model.world.TileMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.function.IntPredicate;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ExplorationView extends StackPane {

    public static final int TILE_SIZE = 32;

    public static final int ZOOM = 3;

    public static final int VIEW_WIDTH = 960;

    public static final int VIEW_HEIGHT = 720;

    private static final int CELL = TILE_SIZE * ZOOM;

    private static final double STEP_SECONDS = 0.16;
    private static final double WALK_FRAME_SECONDS = 0.16;

    private final TileMap map;
    private final Player player;
    private final String zonePrefix;
    private final SpriteStore sprites = new SpriteStore();
    private final Canvas canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
    private final Deque<Direction> heldDirections = new ArrayDeque<>();
    private final DiscoveredArea discovered;
    private final Minimap minimap;

    private boolean stepping;
    private double stepProgress;
    private int stepFromX;
    private int stepFromY;
    private double walkTime;
    private long lastFrame;
    private boolean running;
    private StepListener stepListener;
    private IntPredicate portalOpen = zone -> true;

    private final AnimationTimer loop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            double elapsed = lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000.0;
            lastFrame = now;
            update(elapsed);
            if (running) {
                render();
            }
        }
    };

    public ExplorationView(TileMap map, Player player, String zonePrefix, DiscoveredArea discovered) {
        this.map = map;
        this.player = player;
        this.zonePrefix = zonePrefix;
        this.discovered = discovered;
        this.minimap = new Minimap(map, discovered);
        this.canvas.getGraphicsContext2D().setImageSmoothing(false);
        getChildren().add(canvas);
        setFocusTraversable(true);
        setOnKeyPressed(this::onKeyPressed);
        setOnKeyReleased(this::onKeyReleased);
        focusedProperty().addListener((obs, was, focused) -> {
            if (!focused) {
                heldDirections.clear();
            }
        });
    }

    @FunctionalInterface
    public interface StepListener {

        void onStep(int x, int y);
    }

    public void setOnStep(StepListener listener) {
        this.stepListener = listener;
    }

    public void setPortalOpen(IntPredicate portalOpen) {
        this.portalOpen = portalOpen;
    }

    public void start() {
        requestFocus();
        lastFrame = 0;
        running = true;
        loop.start();
    }

    public void stop() {
        running = false;
        loop.stop();
        heldDirections.clear();
        stepping = false;
    }

    private void onKeyPressed(KeyEvent event) {
        Direction direction = directionOf(event.getCode());
        if (direction != null && !heldDirections.contains(direction)) {
            heldDirections.push(direction);
        }
    }

    private void onKeyReleased(KeyEvent event) {
        Direction direction = directionOf(event.getCode());
        if (direction != null) {
            heldDirections.remove(direction);
        }
    }

    private static Direction directionOf(KeyCode code) {
        return switch (code) {
            case Z, UP -> Direction.UP;
            case S, DOWN -> Direction.DOWN;
            case Q, LEFT -> Direction.LEFT;
            case D, RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    private void update(double elapsed) {
        if (stepping) {
            stepProgress += elapsed / STEP_SECONDS;
            walkTime += elapsed;
            if (stepProgress >= 1) {
                stepping = false;
                if (stepListener != null) {
                    stepListener.onStep(player.getX(), player.getY());
                }
            }
        }
        if (running && !stepping && !heldDirections.isEmpty()) {
            stepFromX = player.getX();
            stepFromY = player.getY();
            if (player.move(heldDirections.peek(), map)) {
                stepping = true;
                stepProgress = 0;
            }
        }
        if (!stepping) {
            walkTime = 0;
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

        double playerX = player.getX();
        double playerY = player.getY();
        if (stepping) {
            playerX = stepFromX + (playerX - stepFromX) * stepProgress;
            playerY = stepFromY + (playerY - stepFromY) * stepProgress;
        }
        long cameraX = Math.round(Camera.offset((playerX + 0.5) * CELL, VIEW_WIDTH, map.getWidth() * CELL));
        long cameraY = Math.round(Camera.offset((playerY + 0.5) * CELL, VIEW_HEIGHT, map.getHeight() * CELL));

        int firstX = (int) Math.max(0, Math.floorDiv(cameraX, CELL));
        int firstY = (int) Math.max(0, Math.floorDiv(cameraY, CELL));
        int lastX = (int) Math.min(map.getWidth() - 1, Math.floorDiv(cameraX + VIEW_WIDTH - 1, CELL));
        int lastY = (int) Math.min(map.getHeight() - 1, Math.floorDiv(cameraY + VIEW_HEIGHT - 1, CELL));
        discovered.reveal(firstX, firstY, lastX, lastY);
        for (int y = firstY; y <= lastY; y++) {
            for (int x = firstX; x <= lastX; x++) {
                drawTile(gc, x, y, x * CELL - cameraX, y * CELL - cameraY);
            }
        }
        sprites.draw(gc, playerSprite(), Math.round(playerX * CELL) - cameraX, Math.round(playerY * CELL) - cameraY,
                CELL, Color.DODGERBLUE);
        minimap.draw(gc, VIEW_WIDTH, playerX, playerY);
    }

    private void drawTile(GraphicsContext gc, int x, int y, double px, double py) {
        Tile tile = map.tileAt(x, y);
        switch (tile) {
            case WALL -> sprites.draw(gc, tileSprite("wall"), px, py, CELL, Color.DARKSLATEGRAY);
            case STATION -> sprites.draw(gc, tileSprite("station"), px, py, CELL, Color.SEAGREEN);
            case ENCOUNTER -> sprites.draw(gc, tileSprite(encounterName()), px, py, CELL, Color.FORESTGREEN);
            case PORTAL -> {
                boolean on = portalOpen.test(map.portalZoneAt(x, y));
                sprites.draw(gc, on ? "tiles/portal_on.png" : "tiles/portal_off.png", px, py, CELL,
                        on ? Color.DEEPSKYBLUE : Color.MIDNIGHTBLUE);
            }
            case BOSS -> {
                sprites.draw(gc, tileSprite(floorName()), px, py, CELL, Color.BURLYWOOD);
                sprites.draw(gc, "robots/chef_nexus/map/idle_down.png", px, py, CELL, Color.CRIMSON);
            }
            default -> sprites.draw(gc, tileSprite(floorName()), px, py, CELL, Color.BURLYWOOD);
        }
    }

    private String floorName() {
        return "home".equals(zonePrefix) ? "floor" : "ground";
    }

    private String encounterName() {
        return switch (zonePrefix) {
            case "z2" -> "hazard";
            case "z3" -> "data";
            default -> "grass";
        };
    }

    private String tileSprite(String name) {
        return "tiles/" + zonePrefix + "_" + name + ".png";
    }

    private String playerSprite() {
        String facing = player.getFacing().name().toLowerCase(Locale.ROOT);
        if (!stepping) {
            return "characters/leon/idle_" + facing + ".png";
        }
        int frame = (int) (walkTime / WALK_FRAME_SECONDS) % 2 + 1;
        return "characters/leon/walk_" + facing + "_" + frame + ".png";
    }
}
