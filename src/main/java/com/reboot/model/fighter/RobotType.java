package com.reboot.model.fighter;

/**
 * Les trois types de robots. L'Infighter bat le Healer, le Healer bat le Tank et le Tank bat l'Infighter.
 */
public enum RobotType {
    /** Encaisse les coups. */
    TANK,
    /** Soigne. */
    HEALER,
    /** Frappe fort et vite. */
    INFIGHTER;

    /** Le multiplicateur de dégâts du type fort contre le type qu'il bat. */
    public static final double ADVANTAGE_BONUS = 1.5;

    /**
     * {@return {@code true} si ce type bat l'autre}
     *
     * @param other le type adverse
     */
    public boolean beats(RobotType other) {
        return switch (this) {
            case TANK -> other == INFIGHTER;
            case INFIGHTER -> other == HEALER;
            case HEALER -> other == TANK;
        };
    }

    /**
     * {@return le multiplicateur de dégâts contre l'autre type}
     *
     * @param other le type adverse
     */
    public double multiplierAgainst(RobotType other) {
        return beats(other) ? ADVANTAGE_BONUS : 1.0;
    }
}