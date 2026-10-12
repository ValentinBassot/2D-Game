package com.reboot.view;

public class DiscoveredArea {

    private final boolean[][] seen;

    public DiscoveredArea(int width, int height) {
        this.seen = new boolean[height][width];
    }

    public void reveal(int firstX, int firstY, int lastX, int lastY) {
        for (int y = Math.max(0, firstY); y <= Math.min(seen.length - 1, lastY); y++) {
            for (int x = Math.max(0, firstX); x <= Math.min(seen[y].length - 1, lastX); x++) {
                seen[y][x] = true;
            }
        }
    }

    public boolean isDiscovered(int x, int y) {
        return y >= 0 && y < seen.length && x >= 0 && x < seen[y].length && seen[y][x];
    }
}
