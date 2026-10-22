package com.reboot.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.TankBot;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChoiceResolverTest {

    private final Game game = Game.newGame();
    private final ChoiceResolver resolver = new ChoiceResolver(game);

    private static HealerBot defeated(int level) {
        HealerBot robot = new HealerBot(level);
        robot.takeDamage(999);
        return robot;
    }

    @Test
    void reprogram_turnsTheRobotIntoARepairedTeammate() {
        HealerBot robot = defeated(3);

        ChoiceOutcome outcome = resolver.reprogram(robot);

        assertTrue(outcome.reprogrammed());
        assertTrue(robot.canFightForPlayer());
        assertEquals(robot.getMaxHp(), robot.getHp());
        assertEquals(3, robot.getLevel());
        assertTrue(game.getTeam().getRobots().contains(robot));
        assertEquals("HealerBot rejoint ton équipe.", outcome.lines().get(0));
    }

    @Test
    void reprogram_lowersTheWantedBar_andGivesNoPart() {
        resolver.finish(defeated(1));
        resolver.reprogram(defeated(1));
        assertEquals(10, game.getWantedBar().getValue());
        assertEquals(1, game.getParts().getParts());
        assertEquals(1, game.getDataCenter().getRobotsKilled());
    }

    @Test
    void whenTheTeamIsFull_theRobotGoesToTheHome() {
        resolver.reprogram(defeated(1));
        resolver.reprogram(defeated(1));
        HealerBot fourth = defeated(1);

        ChoiceOutcome outcome = resolver.reprogram(fourth);

        assertEquals(List.of(fourth), game.getHome().getStored());
        assertTrue(outcome.lines().get(0).contains("Home"));
    }

    @Test
    void finish_givesParts_raisesTheBar_andFeedsTheNetwork() {
        ChoiceOutcome outcome = resolver.finish(defeated(6));

        assertFalse(outcome.reprogrammed());
        assertEquals(15, game.getWantedBar().getValue());
        assertEquals(3, game.getParts().getParts());
        assertEquals(1, game.getDataCenter().getRobotsKilled());
        assertEquals("Barre de recherche : 0 -> 15", outcome.lines().get(1));
        assertEquals(1, game.getTeam().getRobots().size());
    }

    @Test
    void previews_describeTheConsequences_beforeTheChoice() {
        HealerBot robot = defeated(6);
        assertEquals("- 5 recherche · rejoint l'équipe", resolver.previewReprogram(robot));
        assertEquals("+ 15 recherche · + 3 pièces · réseau + 1", resolver.previewFinish(robot));
        assertEquals(0, game.getWantedBar().getValue());
    }

    @Test
    void anAlly_cannotBeChosenAgain() {
        HealerBot robot = defeated(1);
        resolver.reprogram(robot);
        assertThrows(IllegalStateException.class, () -> resolver.reprogram(robot));
        assertThrows(IllegalStateException.class, () -> resolver.finish(robot));
        TankBot ally = new TankBot(1);
        ally.reprogram();
        assertThrows(IllegalStateException.class, () -> resolver.finish(ally));
    }
}
