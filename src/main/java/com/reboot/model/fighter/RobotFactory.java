package com.reboot.model.fighter;

import java.util.List;
import java.util.Random;

/**
 * Fabrique des robots rencontrés dans une zone (pattern Factory).
 *
 * <p>Le reste du jeu demande juste « un robot de cette zone » sans connaître les classes concrètes. Si on
 * ajoute un type de robot, il n'y a que cette classe à modifier.
 */
public class RobotFactory {

    private final Random random;

    /**
     * Crée une fabrique de robots.
     *
     * @param random le générateur, injecté pour que les tests soient reproductibles
     */
    public RobotFactory(Random random) {
        this.random = random;
    }

    /**
     * Calcule le niveau des robots rencontrés, c'est-à-dire le niveau de la zone plus le bonus du réseau
     * NEXUS.
     *
     * @param zoneLevel le niveau de la zone
     * @param networkBonus le bonus du data center, qui grandit avec les robots achevés
     * @return le niveau des robots rencontrés, au moins 1
     */
    public static int encounterLevel(int zoneLevel, int networkBonus) {
        return Math.max(1, zoneLevel + networkBonus);
    }

    /**
     * Crée un robot détourné d'un type donné.
     *
     * @param type le type du robot
     * @param level son niveau
     * @return le robot créé
     */
    public Robot create(RobotType type, int level) {
        return switch (type) {
            case TANK -> new TankBot(level);
            case HEALER -> new HealerBot(level);
            case INFIGHTER -> new InfighterBot(level);
        };
    }

    /**
     * Crée un robot rencontré, d'un des types dominants de la zone tiré au hasard.
     *
     * @param dominantTypes les types de robots présents dans la zone
     * @param level le niveau des robots rencontrés
     * @return le robot créé
     */
    public Robot createEncounter(List<RobotType> dominantTypes, int level) {
        if (dominantTypes.isEmpty()) {
            throw new IllegalArgumentException("A zone needs at least one robot type");
        }
        return create(dominantTypes.get(random.nextInt(dominantTypes.size())), level);
    }
}
