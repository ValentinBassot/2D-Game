package com.reboot.view;

/** Calcule le décalage de la caméra pour centrer le joueur, en s'arrêtant aux bords de la carte. */
public final class Camera {

    private Camera() {
    }

    /**
     * {@return le décalage de la caméra, en pixels}
     *
     * @param target la position à centrer
     * @param viewSize la taille de la vue
     * @param worldSize la taille de la carte
     */
    public static double offset(double target, double viewSize, double worldSize) {
        if (worldSize <= viewSize) {
            return (worldSize - viewSize) / 2;
        }
        return Math.max(0, Math.min(worldSize - viewSize, target - viewSize / 2));
    }
}
