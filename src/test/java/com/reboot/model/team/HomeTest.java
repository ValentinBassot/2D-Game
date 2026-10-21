package com.reboot.model.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void storedRobots_cannotBeModifiedFromOutside() {
        Home home = new Home();
        assertThrows(UnsupportedOperationException.class, () -> home.getStored().add(ally(new TankBot(1))));
    }
}
