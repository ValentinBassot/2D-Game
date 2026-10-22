package com.reboot.view;

/** Les cases déjà vues par le joueur dans une zone, pour la minimap. */
public class DiscoveredArea {

    private final boolean[][] seen;

    /**
     * Crée une zone encore inexplorée.
     *
     * @param width la largeur de la carte
     * @param height la hauteur de la carte
     */
    public DiscoveredArea(int width, int height) {
        this.seen = new boolean[height][width];
    }

    /**
     * Marque un rectangle de cases comme découvertes.
     *
     * @param firstX la première colonne
     * @param firstY la première ligne
     * @param lastX la dernière colonne
     * @param lastY la dernière ligne
     */
    public void reveal(int firstX, int firstY, int lastX, int lastY) {
        for (int y = Math.max(0, firstY); y <= Math.min(seen.length - 1, lastY); y++) {
            for (int x = Math.max(0, firstX); x <= Math.min(seen[y].length - 1, lastX); x++) {
                seen[y][x] = true;
            }
        }
    }

    /**
     * {@return {@code true} si la case a été vue}
     *
     * @param x la colonne
     * @param y la ligne
     */
    public boolean isDiscovered(int x, int y) {
        return y >= 0 && y < seen.length && x >= 0 && x < seen[y].length && seen[y][x];
    }
}
