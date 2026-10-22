package com.reboot.model.world;

/** Les cases d'une carte, lues depuis les fichiers texte. */
public enum Tile {
    /** Le sol. */
    GROUND(true),
    /** Un mur. */
    WALL(false),
    /** Case de rencontre (herbes, zone dangereuse ou données selon la zone). */
    ENCOUNTER(true),
    /** Le poste de la Home. */
    STATION(false),
    /** Un portail vers une zone. */
    PORTAL(true),
    /** Le chef de la zone. */
    CHIEF(false),
    /** Le chef de NEXUS. */
    BOSS(false);

    private final boolean walkable;

    Tile(boolean walkable) {
        this.walkable = walkable;
    }

    /** {@return {@code true} si Léon peut marcher dessus} */
    public boolean isWalkable() { return walkable; }

    /**
     * {@return la case qui correspond à un symbole du fichier de carte}
     *
     * @param symbol le caractère lu
     * @throws IllegalArgumentException si le symbole est inconnu
     */
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
