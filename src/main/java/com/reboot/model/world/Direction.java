package com.reboot.model.world;


/** Les quatre directions de déplacement sur la carte. */
public enum Direction {
    /** Vers le haut. */
    UP(0, -1),
    /** Vers le bas. */
    DOWN(0, 1),
    /** Vers la gauche. */
    LEFT(-1, 0),
    /** Vers la droite. */
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** {@return le déplacement horizontal, en cases} */
    public int getDx() { return dx; }

    /** {@return le déplacement vertical, en cases} */
    public int getDy() { return dy; }
}
