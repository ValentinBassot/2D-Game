package com.reboot.model.fighter;

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

        @Override
        public boolean isReprogrammable() {
            return false;
        }

        @Override
        public String getSpriteId() {
            return "dummy";
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
    void defenseBoost_multipliesDef_untilItsTurnsAreOver() {
        Combattant c = new TestFighter(1);
        c.boostDefense(2, 2);
        assertEquals(24, c.getDef());
        c.endTurn();
        assertEquals(24, c.getDef());
        c.endTurn();
        assertEquals(12, c.getDef());
    }

    @Test
    void invalidDefenseBoost_isRejected() {
        Combattant c = new TestFighter(1);
        assertThrows(IllegalArgumentException.class, () -> c.boostDefense(0, 2));
        assertThrows(IllegalArgumentException.class, () -> c.boostDefense(2, 0));
    }

    @Test
    void enoughXp_raisesTheLevel_andAllTheStats() {
        Combattant c = new TestFighter(1);
        c.takeDamage(10);

        assertEquals(1, c.gainXp(50));

        assertEquals(2, c.getLevel());
        assertEquals(66, c.getMaxHp());
        assertEquals(56, c.getHp());
        assertEquals(13, c.getDef());
        assertEquals(0, c.getXp());
    }

    @Test
    void theXpNeeded_growsWithTheLevel_andTheRestIsKept() {
        Combattant c = new TestFighter(2);
        assertEquals(100, c.getXpToNextLevel());
        assertEquals(0, c.gainXp(90));
        assertEquals(2, c.gainXp(260));
        assertEquals(4, c.getLevel());
        assertEquals(100, c.getXp());
    }

    @Test
    void aKnockedOutFighter_levelsUp_withoutComingBack() {
        Combattant c = new TestFighter(1);
        c.takeDamage(999);
        c.gainXp(50);
        assertTrue(c.isKo());
    }

    @Test
    void theXpReward_isTenTimesTheLevel() {
        assertEquals(40, new TestFighter(4).getXpReward());
    }

    @Test
    void negativeXp_isRejected() {
        Combattant c = new TestFighter(1);
        assertThrows(IllegalArgumentException.class, () -> c.gainXp(-1));
    }

    @Test
    void negativeDamage_isRejected() {
        Combattant c = new TestFighter(1);
        assertThrows(IllegalArgumentException.class, () -> c.takeDamage(-5));
    }
}