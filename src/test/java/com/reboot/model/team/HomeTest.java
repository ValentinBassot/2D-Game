package com.reboot.model.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import java.util.List;
import org.junit.jupiter.api.Test;

class HomeTest {

    private static <T extends Robot> T ally(T robot) {
        robot.reprogram();
        return robot;
    }

    @Test
    void anAlly_canBeStored() {
        Home home = new Home();
        TankBot tank = ally(new TankBot(1));
        home.store(tank);
        assertEquals(List.of(tank), home.getStored());
    }

    @Test
    void aHijackedRobot_orTheSameRobotTwice_isRejected() {
        Home home = new Home();
        assertThrows(IllegalArgumentException.class, () -> home.store(new TankBot(1)));
        TankBot tank = ally(new TankBot(1));
        home.store(tank);
        assertThrows(IllegalArgumentException.class, () -> home.store(tank));
    }

    @Test
    void swap_exchangesATeamRobotWithAStoredOne() {
        InfighterBot starter = ally(new InfighterBot(1));
        HealerBot stored = ally(new HealerBot(1));
        Team team = new Team(starter);
        Home home = new Home();
        home.store(stored);

        home.swap(team, starter, stored);

        assertEquals(List.of(stored), team.getRobots());
        assertEquals(List.of(starter), home.getStored());
    }

    @Test
    void swap_withARobotThatIsNotAtTheHome_isRejected() {
        InfighterBot starter = ally(new InfighterBot(1));
        Team team = new Team(starter);
        assertThrows(IllegalArgumentException.class,
                () -> new Home().swap(team, starter, ally(new TankBot(1))));
    }

    @Test
    void fillTeam_replacesTheRobotsUnderRepair_withReadyOnes() {
        InfighterBot broken = ally(new InfighterBot(1));
        TankBot fine = ally(new TankBot(1));
        HealerBot stored = ally(new HealerBot(1));
        Team team = new Team(broken);
        team.add(fine);
        Home home = new Home();
        home.store(stored);
        broken.takeDamage(999);
        broken.handleKnockOut();

        home.fillTeam(team);

        assertEquals(List.of(stored, fine), team.getRobots());
        assertEquals(List.of(broken), home.getStored());
        assertTrue(home.hasNoFighter());
    }

    @Test
    void storedRobots_keepRepairingWithEachVictory() {
        InfighterBot broken = ally(new InfighterBot(1));
        broken.takeDamage(999);
        broken.handleKnockOut();
        Home home = new Home();
        home.store(broken);

        for (int i = 0; i < 3; i++) {
            home.recordVictory();
        }

        assertFalse(home.hasNoFighter());
        assertEquals(broken.getMaxHp(), broken.getHp());
    }

    @Test
    void storedRobots_cannotBeModifiedFromOutside() {
        Home home = new Home();
        assertThrows(UnsupportedOperationException.class, () -> home.getStored().add(ally(new TankBot(1))));
    }
}
