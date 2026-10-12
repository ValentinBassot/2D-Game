package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CombattantTest {

    private static class TestFighter extends Combattant {
        TestFighter(int level) {
            super("Test", level, 60, 8, 12, 4);
        }
    }

    @Test
    void levelOne_hasBaseStats() {
        Combattant c = new TestFighter(1);
        assertEquals(60, c.getMaxHp());
        assertEquals(60, c.getHp());
        assertEquals(8, c.getAtk());
        assertEquals(12, c.getDef());
        assertEquals(4, c.getSpeed());
    }

    @Test
    void levelFive_hasIncreasedStats() {
        assertEquals(84, new TestFighter(5).getMaxHp());
    }

    @Test
    void takeDamage_removesHp() {
        Combattant c = new TestFighter(1);
        c.takeDamage(20);
        assertEquals(40, c.getHp());
        assertFalse(c.isKo());
    }

    @Test
    void hp_neverGoesBelowZero() {
        Combattant c = new TestFighter(1);
        c.takeDamage(999);
        assertEquals(0, c.getHp());
        assertTrue(c.isKo());
    }

    @Test
    void heal_neverExceedsMax() {
        Combattant c = new TestFighter(1);
        c.takeDamage(10);
        c.heal(15);
        assertEquals(60, c.getHp());
    }

    @Test
    void levelZero_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TestFighter(0));
    }

    @Test
    void negativeDamage_isRejected() {
        Combattant c = new TestFighter(1);
        assertThrows(IllegalArgumentException.class, () -> c.takeDamage(-5));
    }
}