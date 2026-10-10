package com.reboot.model;

public enum RobotType {
    TANK,
    HEALER,
    INFIGHTER;

    public static final double ADVANTAGE_BONUS = 1.5;

    public boolean beats(RobotType other) {
        return switch (this) {
            case TANK -> other == INFIGHTER;
            case INFIGHTER -> other == HEALER;
            case HEALER -> other == TANK;
        };
    }

    public double multiplierAgainst(RobotType other) {
        return beats(other) ? ADVANTAGE_BONUS : 1.0;
    }
}