package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

class EncounterTriggerTest {

    @Test
    void pas_de_rencontre_hors_des_cases_de_rencontre() {
        EncounterTrigger always = new EncounterTrigger(new Random(1), 1);
        assertFalse(always.shouldTrigger(Tile.GROUND));
        assertFalse(always.shouldTrigger(Tile.PORTAL));
        assertFalse(always.shouldTrigger(Tile.WALL));
    }

    @Test
    void une_chance_sur_un_declenche_toujours() {
        EncounterTrigger always = new EncounterTrigger(new Random(1), 1);
        for (int i = 0; i < 20; i++) {
            assertTrue(always.shouldTrigger(Tile.ENCOUNTER));
        }
    }

    @Test
    void la_frequence_suit_la_chance_demandee() {
        EncounterTrigger trigger = new EncounterTrigger(new Random(42), 5);
        int encounters = 0;
        for (int i = 0; i < 10_000; i++) {
            if (trigger.shouldTrigger(Tile.ENCOUNTER)) {
                encounters++;
            }
        }
        assertEquals(2000, encounters, 150);
    }

    @Test
    void une_chance_nulle_est_refusee() {
        assertThrows(IllegalArgumentException.class, () -> new EncounterTrigger(new Random(), 0));
    }
}
