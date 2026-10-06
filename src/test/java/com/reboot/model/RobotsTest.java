package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RobotsTest {

    @Test
    void tankBot_aLesStatsDuGdd() {
        TankBot tank = new TankBot(1);
        assertEquals(TypeRobot.TANK, tank.getType());
        assertEquals(60, tank.getPvMax());
        assertEquals(8, tank.getAtk());
        assertEquals(12, tank.getDef());
        assertEquals(4, tank.getVit());
    }

    @Test
    void healerBot_aLesStatsDuGdd() {
        HealerBot healer = new HealerBot(1);
        assertEquals(TypeRobot.HEALER, healer.getType());
        assertEquals(45, healer.getPvMax());
        assertEquals(7, healer.getAtk());
        assertEquals(8, healer.getDef());
        assertEquals(9, healer.getVit());
    }

    @Test
    void infighterBot_aLesStatsDuGdd() {
        InfighterBot infighter = new InfighterBot(1);
        assertEquals(TypeRobot.INFIGHTER, infighter.getType());
        assertEquals(45, infighter.getPvMax());
        assertEquals(14, infighter.getAtk());
        assertEquals(6, infighter.getDef());
        assertEquals(10, infighter.getVit());
    }

    @Test
    void tousLesRobots_ontFrappeEnPremier_plusDeuxCompetences() {
        Robot[] robots = {new TankBot(1), new HealerBot(1), new InfighterBot(1)};
        for (Robot robot : robots) {
            assertEquals(3, robot.getCompetences().size());
            assertEquals(Robot.FRAPPE, robot.getCompetences().get(0));
        }
    }

    @Test
    void leSacDeCompetences_nePeutPasEtreModifieDeLExterieur() {
        TankBot tank = new TankBot(1);
        assertThrows(UnsupportedOperationException.class,
                () -> tank.getCompetences().add(Robot.FRAPPE));
    }

    @Test
    void unBot_heriteDesMethodesDeCombattant() {
        TankBot tank = new TankBot(5);
        assertEquals(84, tank.getPvMax());
        tank.recevoirDegats(100);
        assertTrue(tank.estKO());
    }
}