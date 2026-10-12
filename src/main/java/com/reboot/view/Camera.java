package com.reboot.view;

public final class Camera {

    private Camera() {
    }

    public static double offset(double target, double viewSize, double worldSize) {
        if (worldSize <= viewSize) {
            return (worldSize - viewSize) / 2;
        }
        return Math.max(0, Math.min(worldSize - viewSize, target - viewSize / 2));
    }
}
