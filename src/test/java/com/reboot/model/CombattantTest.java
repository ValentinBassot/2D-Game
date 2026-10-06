package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CombattantTest {

    /** Un enfant "bidon", uniquement pour pouvoir tester Combattant. */
    private static class CombattantDeTest extends Combattant {
        CombattantDeTest(int niveau) {
            // mêmes stats de base que le TankBot : 60 PV, 8 ATK, 12 DEF, 4 VIT
            super("Test", niveau, 60, 8, 12, 4);
        }
    }

    @Test
    void niveau1_aSesStatsDeBase() {
        Combattant c = new CombattantDeTest(1);
        assertEquals(60, c.getPvMax());
        assertEquals(60, c.getPv());
        assertEquals(8, c.getAtk());
        assertEquals(12, c.getDef());
        assertEquals(4, c.getVit());
    }

    @Test
    void niveau5_aSesStatsAugmentees() {
        Combattant c = new CombattantDeTest(5);
        assertEquals(84, c.getPvMax());   // 60 + 60*4/10
    }

    @Test
    void recevoirDegats_enleveDesPv() {
        Combattant c = new CombattantDeTest(1);
        c.recevoirDegats(20);
        assertEquals(40, c.getPv());
        assertFalse(c.estKO());
    }

    @Test
    void lesPv_neDescendentJamaisSousZero() {
        Combattant c = new CombattantDeTest(1);
        c.recevoirDegats(999);
        assertEquals(0, c.getPv());
        assertTrue(c.estKO());
    }

    @Test
    void soigner_neDepassePasLeMaximum() {
        Combattant c = new CombattantDeTest(1);
        c.recevoirDegats(10);   // 50 PV
        c.soigner(15);          // 65 ? non : plafonné à 60
        assertEquals(60, c.getPv());
    }

    @Test
    void niveauZero_estRefuse() {
        assertThrows(IllegalArgumentException.class, () -> new CombattantDeTest(0));
    }

    @Test
    void degatsNegatifs_sontRefuses() {
        Combattant c = new CombattantDeTest(1);
        assertThrows(IllegalArgumentException.class, () -> c.recevoirDegats(-5));
    }
}