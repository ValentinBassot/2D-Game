package com.reboot.model.fighter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class BossTest {

    @Test
    void theNexusChief_hasAFixedLevelAndStrongStats() {
        Boss boss = new Boss();
        assertEquals("Chef de NEXUS", boss.getName());
        assertEquals(Boss.FIXED_LEVEL, boss.getLevel());
        assertEquals(152, boss.getMaxHp());
        assertEquals(24, boss.getAtk());
        assertEquals(22, boss.getDef());
        assertEquals(15, boss.getSpeed());
    }

    @Test
    void theBoss_isAnExosquelette_withItsSkills() {
        Exosquelette boss = new Boss();
        assertTrue(boss.getSkills().contains(Exosquelette.OVERLOAD));
        assertFalse(boss.isReprogrammable());
    }

    @Test
    void onlyTheBoss_endsTheGame() {
        assertTrue(new Boss().endsTheGame());
        assertFalse(new Exosquelette(4).endsTheGame());
        assertFalse(new TankBot(1).endsTheGame());
    }
}
