package com.reboot.model;

import java.util.Random;

public class EncounterTrigger {

    private final Random random;
    private final int oneIn;

    public EncounterTrigger(Random random, int oneIn) {
        if (oneIn < 1) {
            throw new IllegalArgumentException("oneIn must be at least 1");
        }
        this.random = random;
        this.oneIn = oneIn;
    }

    public boolean shouldTrigger(Tile tile) {
        return tile == Tile.ENCOUNTER && random.nextInt(oneIn) == 0;
    }
}
