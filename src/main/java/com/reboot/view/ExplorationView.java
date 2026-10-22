package com.reboot.view;

import com.reboot.model.settings.Action;
import com.reboot.model.settings.KeyBindings;
import com.reboot.model.world.Direction;
import com.reboot.model.world.Player;
import com.reboot.model.world.Tile;
import com.reboot.model.world.TileMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.IntPredicate;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Écran d'exploration avec la carte autour de Léon, la minimap et le HUD. La boucle de jeu lit les touches,
 * met à jour le déplacement et redessine.
 */
public class ExplorationView extends StackPane {

    /** La taille d'une tuile dans les images, en pixels. */
    public static final int TILE_SIZE = 32;

    /** Le zoom appliqué aux tuiles. */
    public static final int ZOOM = 3;

    /** La largeur de base de l'écran. */
    public static final int VIEW_WIDTH = 960;

    /** La hauteur de base de l'écran. */
    public static final int VIEW_HEIGHT = 720;

    private static final int CELL = TILE_SIZE * ZOOM;

    private static final double STEP_SECONDS = 0.16;
    private static final double WALK_FRAME_SECONDS = 0.16;
    private static final double MESSAGE_SECONDS = 3.5;

    private final TileMap map;
    private final Player player;
    private final String zonePrefix;
    private final SpriteStore sprites = new SpriteStore();
    private final Canvas canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
    private final Deque<Direction> heldDirections = new ArrayDeque<>();
    private final DiscoveredArea discovered;
    private final Minimap minimap;
    private final KeyBindings keys;

    private boolean stepping;
    private double stepProgress;
    private int stepFromX;
    private int stepFromY;
    private double walkTime;
    private long lastFrame;
    private boolean running;
    private TileListener stepListener;
    private IntPredicate portalOpen = zone -> true;
    private Hud hud;
    private Runnable onPause = () -> { };
    private TileListener bumpListener;
    private BooleanSupplier guardianPresent = () -> true;
    private BooleanSupplier guardianOpen = () -> true;
    private String message;
    private double messageTimeLeft;
    private String zoneName = "";

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

    /**
     * Crée la vue d'une zone.
     *
     * @param map la carte
     * @param player Léon
     * @param zonePrefix le préfixe des tuiles de la zone ({@code z1}, {@code home}...)
     * @param discovered les cases déjà vues
     * @param keys les touches du joueur
     */
    public ExplorationView(TileMap map, Player player, String zonePrefix, DiscoveredArea discovered,
                           KeyBindings keys) {
        this.keys = keys;
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

    /** Reçoit une case de la carte. */
    @FunctionalInterface
    public interface TileListener {

        /**
         * Appelée avec la case concernée.
         *
         * @param x la colonne
         * @param y la ligne
         */
        void onTile(int x, int y);
    }

    /**
     * Prévient à chaque pas terminé.
     *
     * @param listener reçoit la case d'arrivée
     */
    public void setOnStep(TileListener listener) {
        this.stepListener = listener;
    }

    /**
     * Choisit quels portails sont allumés.
     *
     * @param portalOpen reçoit le numéro de zone du portail
     */
    public void setPortalOpen(IntPredicate portalOpen) {
        this.portalOpen = portalOpen;
    }

    /**
     * Prévient quand Léon se heurte à une case bloquée, comme un chef de zone.
     *
     * @param listener reçoit la case heurtée
     */
    public void setOnBump(TileListener listener) {
        this.bumpListener = listener;
    }

    /**
     * Décrit le gardien de la zone (chef ou boss).
     *
     * @param present {@code true} tant qu'il n'est pas vaincu
     * @param open {@code true} s'il accepte le combat
     */
    public void setGuardian(BooleanSupplier present, BooleanSupplier open) {
        this.guardianPresent = present;
        this.guardianOpen = open;
    }

    /**
     * Affiche un message quelques secondes au-dessus de la carte.
     *
     * @param text le message
     */
    public void showMessage(String text) {
        this.message = text;
        this.messageTimeLeft = MESSAGE_SECONDS;
    }

    /**
     * Prévient quand le joueur ouvre la pause.
     *
     * @param onPause appelé à l'appui sur la touche Pause
     */
    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
    }

    /**
     * Ajoute le HUD par-dessus la carte.
     *
     * @param hud le HUD
     * @param zoneName le nom de la zone affiché
     */
    public void setHud(Hud hud, String zoneName) {
        this.hud = hud;
        this.zoneName = zoneName;
    }

    /** Démarre la boucle de jeu et prend le focus du clavier. */
    public void start() {
        requestFocus();
        lastFrame = 0;
        running = true;
        loop.start();
    }

    /** Arrête la boucle de jeu et oublie les touches enfoncées. */
    public void stop() {
        running = false;
        loop.stop();
        heldDirections.clear();
        stepping = false;
    }

    private void onKeyPressed(KeyEvent event) {
        if (keys.actionOf(event.getCode().name()).orElse(null) == Action.PAUSE) {
            onPause.run();
            return;
        }
        Direction direction = directionOf(event);
        if (direction != null && !heldDirections.contains(direction)) {
            heldDirections.push(direction);
            int targetX = player.getX() + direction.getDx();
            int targetY = player.getY() + direction.getDy();
            if (!stepping && !map.isWalkable(targetX, targetY) && bumpListener != null) {
                bumpListener.onTile(targetX, targetY);
            }
        }
    }

    private void onKeyReleased(KeyEvent event) {
        Direction direction = directionOf(event);
        if (direction != null) {
            heldDirections.remove(direction);
        }
    }

    private Direction directionOf(KeyEvent event) {
        return keys.actionOf(event.getCode().name()).map(action -> switch (action) {
            case MOVE_UP -> Direction.UP;
            case MOVE_DOWN -> Direction.DOWN;
            case MOVE_LEFT -> Direction.LEFT;
            case MOVE_RIGHT -> Direction.RIGHT;
            default -> null;
        }).orElse(null);
    }

    private void update(double elapsed) {
        messageTimeLeft = Math.max(0, messageTimeLeft - elapsed);
        if (stepping) {
            stepProgress += elapsed / STEP_SECONDS;
            walkTime += elapsed;
            if (stepProgress >= 1) {
                stepping = false;
                if (stepListener != null) {
                    stepListener.onTile(player.getX(), player.getY());
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
        if (hud != null) {
            hud.draw(gc, zoneName, VIEW_WIDTH, VIEW_HEIGHT);
        }
        if (messageTimeLeft > 0 && message != null) {
            drawMessage(gc);
        }
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
            case CHIEF -> drawGuardian(gc, "robots/exosquelette/map/idle_down.png", px, py);
            case BOSS -> drawGuardian(gc, "robots/chef_nexus/map/idle_down.png", px, py);
            default -> sprites.draw(gc, tileSprite(floorName()), px, py, CELL, Color.BURLYWOOD);
        }
    }

    private String floorName() {
        return "home".equals(zonePrefix) ? "floor" : "ground";
    }

    private void drawGuardian(GraphicsContext gc, String sprite, double px, double py) {
        sprites.draw(gc, tileSprite(floorName()), px, py, CELL, Color.BURLYWOOD);
        if (!guardianPresent.getAsBoolean()) {
            return;
        }
        sprites.draw(gc, sprite, px, py, CELL, Color.CRIMSON);
        if (!guardianOpen.getAsBoolean()) {
            gc.setFill(Color.rgb(10, 14, 28, 0.55));
            gc.fillRect(px, py, CELL, CELL);
            gc.setFill(Color.rgb(255, 200, 80));
            gc.fillRect(px + CELL / 2.0 - 12, py + CELL / 2.0 - 4, 24, 20);
            gc.setStroke(Color.rgb(255, 200, 80));
            gc.setLineWidth(4);
            gc.strokeArc(px + CELL / 2.0 - 9, py + CELL / 2.0 - 20, 18, 26, 0, 180, ArcType.OPEN);
        }
    }

    private void drawMessage(GraphicsContext gc) {
        double width = 760;
        double x = (VIEW_WIDTH - width) / 2;
        double y = 186;
        gc.setFill(Color.rgb(10, 14, 28, 0.88));
        gc.fillRect(x, y, width, 44);
        gc.setStroke(Color.rgb(255, 200, 80));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, 44);
        gc.setFill(Color.rgb(235, 238, 250));
        gc.setFont(Font.font("Monospaced", 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(message, VIEW_WIDTH / 2.0, y + 22);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
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
