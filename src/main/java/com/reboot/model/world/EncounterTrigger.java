package com.reboot.model.world;

import java.util.Random;

/**
 * Décide si une case de rencontre lance un combat. Le {@code Random} est passé en paramètre pour avoir des
 * tests reproductibles.
 */
public class EncounterTrigger {

    private final Random random;
    private final int oneIn;

    /**
     * Crée un déclencheur.
     *
     * @param random le générateur
     * @param oneIn une chance sur combien de déclencher un combat, au moins 1
     */
    public EncounterTrigger(Random random, int oneIn) {
        if (oneIn < 1) {
            throw new IllegalArgumentException("oneIn must be at least 1");
        }
        this.random = random;
        this.oneIn = oneIn;
    }

    /**
     * {@return {@code true} si un combat commence}
     *
     * @param tile la case où le joueur vient d'arriver
     */
    public boolean shouldTrigger(Tile tile) {
        return tile == Tile.ENCOUNTER && random.nextInt(oneIn) == 0;
    }
}
