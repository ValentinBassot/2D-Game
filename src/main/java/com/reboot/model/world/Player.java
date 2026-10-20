package com.reboot.model.world;

public class Player {

    private int x;
    private int y;
    private Direction facing = Direction.DOWN;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getFacing() { return facing; }
}
