package com.reboot.model.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class TileMap {

    public static final char SPAWN_SYMBOL = '@';

    public static final int NO_PORTAL = -1;

    private final char[][] symbols;
    private final Tile[][] tiles;
    private final int spawnX;
    private final int spawnY;

    private TileMap(char[][] symbols, Tile[][] tiles, int spawnX, int spawnY) {
        this.symbols = symbols;
        this.tiles = tiles;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public static TileMap load(String path) {
        InputStream stream = TileMap.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Map not found: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parse(reader.lines().filter(line -> !line.isEmpty()).toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static TileMap parse(List<String> rows) {
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("The map is empty");
        }
        int height = rows.size();
        int width = rows.get(0).length();
        char[][] symbols = new char[height][];
        Tile[][] tiles = new Tile[height][width];
        int spawnX = -1;
        int spawnY = -1;
        for (int y = 0; y < height; y++) {
            String row = rows.get(y);
            if (row.length() != width) {
                throw new IllegalArgumentException("Row " + y + " has " + row.length() + " tiles, expected " + width);
            }
            symbols[y] = row.toCharArray();
            for (int x = 0; x < width; x++) {
                tiles[y][x] = Tile.fromSymbol(row.charAt(x));
                if (row.charAt(x) == SPAWN_SYMBOL) {
                    if (spawnX >= 0) {
                        throw new IllegalArgumentException("The map has more than one spawn");
                    }
                    spawnX = x;
                    spawnY = y;
                }
            }
        }
        if (spawnX < 0) {
            throw new IllegalArgumentException("The map has no spawn");
        }
        return new TileMap(symbols, tiles, spawnX, spawnY);
    }

    public int getWidth() { return tiles[0].length; }

    public int getHeight() { return tiles.length; }

    public int getSpawnX() { return spawnX; }

    public int getSpawnY() { return spawnY; }

    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
    }

    public Tile tileAt(int x, int y) {
        return contains(x, y) ? tiles[y][x] : Tile.WALL;
    }

    public boolean isWalkable(int x, int y) {
        return tileAt(x, y).isWalkable();
    }

    public int portalZoneAt(int x, int y) {
        return tileAt(x, y) == Tile.PORTAL ? symbols[y][x] - '0' : NO_PORTAL;
    }
}
