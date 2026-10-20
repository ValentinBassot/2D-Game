package com.reboot.view;

import com.reboot.model.world.Tile;
import com.reboot.model.world.TileMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Minimap {

    private static final double MAX_WIDTH = 180;
    private static final double MAX_HEIGHT = 140;
    private static final double MARGIN = 12;
    private static final double BORDER = 3;

    private static final Color FRAME = Color.rgb(10, 14, 28, 0.8);
    private static final Color GROUND = Color.rgb(201, 160, 110);
    private static final Color WALL = Color.rgb(70, 84, 120);
    private static final Color ENCOUNTER = Color.rgb(72, 170, 84);
    private static final Color STATION = Color.rgb(60, 200, 150);
    private static final Color PORTAL = Color.rgb(60, 180, 255);
    private static final Color PLAYER = Color.rgb(255, 60, 60);

    private final TileMap map;
    private final DiscoveredArea discovered;
    private final double cell;

    public Minimap(TileMap map, DiscoveredArea discovered) {
        this.map = map;
        this.discovered = discovered;
        this.cell = Math.max(1, Math.floor(Math.min(MAX_WIDTH / map.getWidth(), MAX_HEIGHT / map.getHeight())));
    }

    public void draw(GraphicsContext gc, double viewWidth, double playerX, double playerY) {
        double width = map.getWidth() * cell;
        double height = map.getHeight() * cell;
        double left = viewWidth - MARGIN - BORDER - width;
        double top = MARGIN + BORDER;

        gc.setFill(FRAME);
        gc.fillRect(left - BORDER, top - BORDER, width + 2 * BORDER, height + 2 * BORDER);
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (discovered.isDiscovered(x, y)) {
                    gc.setFill(colorOf(map.tileAt(x, y)));
                    gc.fillRect(left + x * cell, top + y * cell, cell, cell);
                }
            }
        }
        double dot = cell + 2;
        gc.setFill(PLAYER);
        gc.fillRect(left + (playerX + 0.5) * cell - dot / 2, top + (playerY + 0.5) * cell - dot / 2, dot, dot);
    }

    private static Color colorOf(Tile tile) {
        return switch (tile) {
            case WALL -> WALL;
            case ENCOUNTER -> ENCOUNTER;
            case STATION -> STATION;
            case PORTAL -> PORTAL;
            default -> GROUND;
        };
    }
}
