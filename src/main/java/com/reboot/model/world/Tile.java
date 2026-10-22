package com.reboot.model.world;

public enum Tile {
    GROUND(true),
    WALL(false),
    ENCOUNTER(true),
    STATION(false),
    PORTAL(true),
    CHIEF(false),
    BOSS(false);

    private final boolean walkable;

    Tile(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable() { return walkable; }

    public static Tile fromSymbol(char symbol) {
        return switch (symbol) {
            case '.', TileMap.SPAWN_SYMBOL -> GROUND;
            case '#' -> WALL;
            case '~' -> ENCOUNTER;
            case 'S' -> STATION;
            case '0', '1', '2', '3' -> PORTAL;
            case 'C' -> CHIEF;
            case 'B' -> BOSS;
            default -> throw new IllegalArgumentException("Unknown map symbol: " + symbol);
        };
    }
}
