package com.reboot.model.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class TeamTest {

    private static <T extends Robot> T ally(T robot) {
        robot.reprogram();
        return robot;
    }

    @Test
    void theStarter_isTheActiveRobot() {
        InfighterBot starter = ally(new InfighterBot(2));
        Team team = new Team(starter);
        assertSame(starter, team.getActive());
        assertTrue(team.hasFighterLeft());
    }

    @Test
    void theTeam_holdsThreeRobotsAtMost() {
        Team team = new Team(ally(new InfighterBot(1)));
        assertTrue(team.add(ally(new TankBot(1))));
        assertTrue(team.add(ally(new HealerBot(1))));
        assertTrue(team.isFull());
        assertFalse(team.add(ally(new TankBot(1))));
        assertEquals(3, team.getRobots().size());
    }

    @Test
    void aHijackedRobot_cannotJoin() {
        assertThrows(IllegalArgumentException.class, () -> new Team(new TankBot(1)));
    }

    @Test
    void theSameRobot_cannotJoinTwice() {
        TankBot tank = ally(new TankBot(1));
        Team team = new Team(tank);
        assertThrows(IllegalArgumentException.class, () -> team.add(tank));
    }

    @Test
    void aKnockedOutRobot_isSkipped_andGoesToRepair() {
        InfighterBot first = ally(new InfighterBot(1));
        TankBot second = ally(new TankBot(1));
        Team team = new Team(first);
        team.add(second);

        first.takeDamage(999);
        assertSame(second, team.getActive());

        team.sendKnockedOutToRepair();
        assertTrue(first.isUnderRepair());
        assertFalse(second.isUnderRepair());
        assertFalse(team.isAllUnderRepair());
    }

    @Test
    void whenEveryRobotIsUnderRepair_noOneCanFight() {
        InfighterBot only = ally(new InfighterBot(1));
        Team team = new Team(only);
        only.takeDamage(999);
        team.sendKnockedOutToRepair();
        assertTrue(team.isAllUnderRepair());
        assertFalse(team.hasFighterLeft());
        assertThrows(IllegalStateException.class, team::getActive);
    }

    @Test
    void victories_countDownTheRepairs() {
        InfighterBot first = ally(new InfighterBot(1));
        TankBot second = ally(new TankBot(1));
        Team team = new Team(first);
        team.add(second);
        first.takeDamage(999);
        team.sendKnockedOutToRepair();

        team.recordVictory();
        team.recordVictory();
        team.recordVictory();

        assertFalse(first.isUnderRepair());
        assertEquals(first.getMaxHp(), first.getHp());
    }

    @Test
    void healAll_healsTheFighters_butNotTheRobotsUnderRepair() {
        InfighterBot hurt = ally(new InfighterBot(1));
        TankBot broken = ally(new TankBot(1));
        Team team = new Team(hurt);
        team.add(broken);
        hurt.takeDamage(10);
        broken.takeDamage(999);
        team.sendKnockedOutToRepair();

        team.healAll();

        assertEquals(hurt.getMaxHp(), hurt.getHp());
        assertEquals(0, broken.getHp());
    }

    @Test
    void replace_keepsThePlace_andRejectsInvalidRobots() {
        InfighterBot first = ally(new InfighterBot(1));
        TankBot second = ally(new TankBot(1));
        HealerBot newcomer = ally(new HealerBot(1));
        Team team = new Team(first);
        team.add(second);

        team.replace(first, newcomer);

        assertSame(newcomer, team.getRobots().get(0));
        assertThrows(IllegalArgumentException.class, () -> team.replace(first, ally(new TankBot(1))));
        assertThrows(IllegalArgumentException.class, () -> team.replace(second, new TankBot(1)));
        assertThrows(IllegalArgumentException.class, () -> team.replace(second, newcomer));
    }

    @Test
    void theRobotList_cannotBeModifiedFromOutside() {
        Team team = new Team(ally(new TankBot(1)));
        assertThrows(UnsupportedOperationException.class, () -> team.getRobots().clear());
    }
}
