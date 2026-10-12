package com.reboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkillTest {

    @Test
    void aSkill_keepsItsInfo() {
        Skill flurry = new Skill("Rafale de coups", 16, false);
        assertEquals("Rafale de coups", flurry.getName());
        assertEquals(16, flurry.getPower());
        assertFalse(flurry.ignoresDefense());
    }

    @Test
    void corrosiveNanobots_ignoreDefense() {
        assertTrue(new Skill("Nanobots corrosifs", 12, true).ignoresDefense());
    }

    @Test
    void negativePower_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Skill("Bug", -1, false));
    }
}