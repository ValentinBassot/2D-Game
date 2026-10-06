package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SkillTest {

    @Test
    void uneCompetence_garderSesInfos() {
        Skill rafale = new Skill("Rafale de coups", 16, false);
        assertEquals("Rafale de coups", rafale.getNom());
        assertEquals(16, rafale.getPuissance());
        assertFalse(rafale.ignoreDef());
    }

    @Test
    void nanobotsCorrosifs_ignoreLaDefense() {
        Skill nanobots = new Skill("Nanobots corrosifs", 12, true);
        assertTrue(nanobots.ignoreDef());
    }

    @Test
    void unePuissanceNegative_estRefusee() {
        assertThrows(IllegalArgumentException.class, () -> new Skill("Bug", -1, false));
    }
}