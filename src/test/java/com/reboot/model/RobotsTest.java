package com.reboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RobotsTest {

    @Test
    void tankBot_hasGddStats() {
        TankBot tank = new TankBot(1);
        assertEquals(RobotType.TANK, tank.getType());
        assertEquals(60, tank.getMaxHp());
        assertEquals(8, tank.getAtk());
        assertEquals(12, tank.getDef());
        assertEquals(4, tank.getSpeed());
    }

    @Test
    void healerBot_hasGddStats() {
        HealerBot healer = new HealerBot(1);
        assertEquals(RobotType.HEALER, healer.getType());
        assertEquals(45, healer.getMaxHp());
        assertEquals(7, healer.getAtk());
        assertEquals(8, healer.getDef());
        assertEquals(9, healer.getSpeed());
    }

    @Test
    void infighterBot_hasGddStats() {
        InfighterBot infighter = new InfighterBot(1);
        assertEquals(RobotType.INFIGHTER, infighter.getType());
        assertEquals(45, infighter.getMaxHp());
        assertEquals(14, infighter.getAtk());
        assertEquals(6, infighter.getDef());
        assertEquals(10, infighter.getSpeed());
    }

    @Test
    void allRobots_haveStrikeFirst_plusTwoSkills() {
        Robot[] robots = {new TankBot(1), new HealerBot(1), new InfighterBot(1)};
        for (Robot robot : robots) {
            assertEquals(3, robot.getSkills().size());
            assertEquals(Robot.STRIKE, robot.getSkills().get(0));
        }
    }

    @Test
    void skillList_cannotBeModifiedFromOutside() {
        TankBot tank = new TankBot(1);
        assertThrows(UnsupportedOperationException.class,
                () -> tank.getSkills().add(Robot.STRIKE));
    }

    @Test
    void aBot_inheritsCombattantMethods() {
        TankBot tank = new TankBot(5);
        assertEquals(84, tank.getMaxHp());
        tank.takeDamage(100);
        assertTrue(tank.isKo());
    }
}