package com.reboot.model.fighter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotStateTest {

    private static TankBot brokenAlly() {
        TankBot robot = new TankBot(1);
        robot.reprogram();
        robot.handleKnockOut();
        return robot;
    }

    @Test
    void newRobot_isHijacked() {
        TankBot robot = new TankBot(1);
        assertInstanceOf(HijackedState.class, robot.getState());
        assertFalse(robot.canFightForPlayer());
        assertTrue(robot.isReprogrammable());
    }

    @Test
    void reprogram_turnsTheSameRobotIntoAnAlly() {
        TankBot robot = new TankBot(3);
        robot.reprogram();
        assertInstanceOf(AllyState.class, robot.getState());
        assertTrue(robot.canFightForPlayer());
        assertFalse(robot.isReprogrammable());
        assertEquals(3, robot.getLevel());
    }

    @Test
    void reprogrammingAnAlly_isRejected() {
        TankBot robot = new TankBot(1);
        robot.reprogram();
        assertThrows(IllegalStateException.class, robot::reprogram);
    }

    @Test
    void hijackedKnockedOut_staysHijacked() {
        TankBot robot = new TankBot(1);
        robot.handleKnockOut();
        assertInstanceOf(HijackedState.class, robot.getState());
    }

    @Test
    void allyKnockedOut_goesToRepair() {
        TankBot robot = brokenAlly();
        assertInstanceOf(RepairingState.class, robot.getState());
        assertFalse(robot.canFightForPlayer());
    }

    @Test
    void repair_takesThreeVictories() {
        TankBot robot = brokenAlly();
        robot.recordTeamVictory();
        robot.recordTeamVictory();
        RepairingState state = assertInstanceOf(RepairingState.class, robot.getState());
        assertEquals(1, state.getVictoriesLeft());
        robot.recordTeamVictory();
        assertInstanceOf(AllyState.class, robot.getState());
    }

    @Test
    void eachPart_replacesOneVictory() {
        TankBot robot = brokenAlly();
        robot.useRepairPart();
        robot.recordTeamVictory();
        robot.recordTeamVictory();
        assertInstanceOf(AllyState.class, robot.getState());
    }

    @Test
    void repairingRobot_cannotBeReprogrammed() {
        TankBot robot = brokenAlly();
        assertThrows(IllegalStateException.class, robot::reprogram);
    }

    @Test
    void onlyARepairingRobot_isUnderRepair() {
        TankBot robot = new TankBot(1);
        assertFalse(robot.isUnderRepair());
        robot.reprogram();
        assertFalse(robot.isUnderRepair());
        robot.handleKnockOut();
        assertTrue(robot.isUnderRepair());
    }

    @Test
    void aRepairedRobot_comesBackWithFullHp() {
        TankBot robot = new TankBot(1);
        robot.reprogram();
        robot.takeDamage(999);
        robot.handleKnockOut();
        robot.recordTeamVictory();
        robot.recordTeamVictory();
        assertEquals(0, robot.getHp());
        robot.useRepairPart();
        assertEquals(robot.getMaxHp(), robot.getHp());
    }

    @Test
    void anAllyWinning_isNotHealed() {
        TankBot robot = new TankBot(1);
        robot.reprogram();
        robot.takeDamage(10);
        robot.recordTeamVictory();
        assertEquals(50, robot.getHp());
    }
}
