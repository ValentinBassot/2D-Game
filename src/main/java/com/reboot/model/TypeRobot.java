package com.reboot.model;

public enum TypeRobot {
    TANK,
    HEALER,
    INFIGHTER;

    public static final double BONUS_AVANTAGE = 1.5;

    public boolean bat(TypeRobot autre) {
        return switch (this) {
            case TANK -> autre == INFIGHTER;
            case INFIGHTER -> autre == HEALER;
            case HEALER -> autre == TANK;
        };
    }

    public double multiplicateurContre(TypeRobot autre) {
        return bat(autre) ? BONUS_AVANTAGE : 1.0;
    }
}