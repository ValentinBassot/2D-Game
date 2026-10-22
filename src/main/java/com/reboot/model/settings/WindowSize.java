package com.reboot.model.settings;

/**
 * Une taille de fenêtre proposée dans les options. Toutes gardent le format 4:3 du jeu.
 *
 * @param width la largeur en pixels
 * @param height la hauteur en pixels
 */
public record WindowSize(int width, int height) {

    /** {@return la taille sous la forme {@code 960 x 720}} */
    public String label() {
        return width + " x " + height;
    }
}
