package com.reboot.model.world;

/** Position et direction de Léon sur la carte. */
public class Player {

    private int x;
    private int y;
    private Direction facing = Direction.DOWN;

    /**
     * Place Léon sur une case.
     *
     * @param x la colonne
     * @param y la ligne
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Fait avancer Léon d'une case. Il se tourne dans la direction même si la case est bloquée.
     *
     * @param direction la direction du pas
     * @param map la carte de la zone
     * @return {@code true} si Léon a bougé
     */
    public boolean move(Direction direction, TileMap map) {
        this.facing = direction;
        int nextX = x + direction.getDx();
        int nextY = y + direction.getDy();
        if (!map.isWalkable(nextX, nextY)) {
            return false;
        }
        this.x = nextX;
        this.y = nextY;
        return true;
    }

    /** {@return sa colonne} */
    public int getX() { return x; }
    /** {@return sa ligne} */
    public int getY() { return y; }
    /** {@return la direction vers laquelle il regarde} */
    public Direction getFacing() { return facing; }
}
