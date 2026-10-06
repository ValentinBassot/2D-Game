package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CalculDegatsTest {

    private static class ExoDeTest extends Combattant {
        ExoDeTest() {
            super("Exo", 1, 60, 8, 12, 4);
        }
    }

    @Test
    void sansAvantage_degatsNormaux() {
        int degats = CalculDegats.calculer(new TankBot(1), TankBot.ECRASEMENT, new HealerBot(1));
        assertEquals(16, degats);
    }

    @Test
    void avecAvantage_bonus1Virgule5() {
        int degats = CalculDegats.calculer(new InfighterBot(1), InfighterBot.RAFALE_DE_COUPS, new HealerBot(1));
        assertEquals(33, degats);
    }

    @Test
    void tankContreInfighter_avecFrappe() {
        int degats = CalculDegats.calculer(new TankBot(1), Robot.FRAPPE, new InfighterBot(1));
        assertEquals(18, degats);
    }

    @Test
    void nanobots_ignorentLaDefense_etArrondissentVersLeBas() {
        int degats = CalculDegats.calculer(new HealerBot(1), HealerBot.NANOBOTS_CORROSIFS, new TankBot(1));
        assertEquals(28, degats);
    }

    @Test
    void armureTropForte_faitQuandMemeUnDegat() {
        int degats = CalculDegats.calculer(new HealerBot(1), Robot.FRAPPE, new TankBot(20));
        assertEquals(1, degats);
    }

    @Test
    void contreUnNonRobot_pasDeBonusDeType() {
        int degats = CalculDegats.calculer(new InfighterBot(1), InfighterBot.RAFALE_DE_COUPS, new ExoDeTest());
        assertEquals(18, degats);
    }

    @Test
    void laCalculatrice_neRetirePasLesPv() {
        HealerBot healer = new HealerBot(1);
        CalculDegats.calculer(new InfighterBot(1), InfighterBot.RAFALE_DE_COUPS, healer);
        assertEquals(45, healer.getPv());
    }
}