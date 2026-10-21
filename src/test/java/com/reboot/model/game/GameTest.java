package com.reboot.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.EncounterResult;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.fighter.Boss;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Exosquelette;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.RepairingState;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import com.reboot.model.team.Team;
import org.junit.jupiter.api.Test;

class GameTest {

    private static EncounterResult victoryAgainst(Combattant enemy) {
        return new EncounterResult(CombatOutcome.VICTORY, enemy, enemy.isReprogrammable());
    }

    private static EncounterResult defeatOf(Robot fallen) {
        fallen.takeDamage(999);
        return new EncounterResult(CombatOutcome.DEFEAT, new InfighterBot(10), false);
    }

    @Test
    void aNewGame_startsWithAnAllyInfighter() {
        Game game = Game.newGame();
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        Robot starter = game.getTeam().getActive();
        assertInstanceOf(InfighterBot.class, starter);
        assertEquals(Game.STARTER_LEVEL, starter.getLevel());
        assertTrue(starter.canFightForPlayer());
    }

    @Test
    void beatingTheNexusChief_winsTheGame() {
        Game game = Game.newGame();
        game.onCombatEnd(victoryAgainst(new Boss()));
        assertEquals(GameStatus.WON, game.getStatus());
        assertTrue(game.isOver());
    }

    @Test
    void beatingARobotOrAZoneChief_doesNotEndTheGame() {
        Game game = Game.newGame();
        game.onCombatEnd(victoryAgainst(new HealerBot(2)));
        game.onCombatEnd(victoryAgainst(new Exosquelette(4)));
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void losingWithTheOnlyRobot_isAGameOver() {
        Game game = Game.newGame();
        game.onCombatEnd(defeatOf(game.getTeam().getActive()));
        assertEquals(GameStatus.LOST, game.getStatus());
    }

    @Test
    void losingWithARobotLeft_isNotAGameOver() {
        Game game = Game.newGame();
        Robot starter = game.getTeam().getActive();
        TankBot backup = new TankBot(2);
        backup.reprogram();
        game.getTeam().add(backup);

        game.onCombatEnd(defeatOf(starter));

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertTrue(starter.isUnderRepair());
        assertEquals(backup, game.getTeam().getActive());
    }

    @Test
    void aRobotStoredAtTheHome_preventsTheGameOver_andJoinsAtTheHome() {
        Game game = Game.newGame();
        Robot starter = game.getTeam().getActive();
        HealerBot stored = new HealerBot(2);
        stored.reprogram();
        game.getHome().store(stored);

        game.onCombatEnd(defeatOf(starter));
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

        game.restAtHome();
        assertEquals(stored, game.getTeam().getActive());
    }

    @Test
    void victories_repairTheFallenRobots() {
        InfighterBot starter = new InfighterBot(2);
        starter.reprogram();
        TankBot backup = new TankBot(2);
        backup.reprogram();
        Team team = new Team(starter);
        team.add(backup);
        Game game = new Game(team);
        game.onCombatEnd(defeatOf(starter));

        for (int i = 0; i < 3; i++) {
            game.onCombatEnd(victoryAgainst(new HealerBot(1)));
        }

        assertFalse(starter.isUnderRepair());
        assertEquals(starter, team.getActive());
    }

    @Test
    void aRobotKnockedOutDuringAWonFight_alsoGoesToRepair() {
        Game game = Game.newGame();
        Robot starter = game.getTeam().getActive();
        TankBot backup = new TankBot(2);
        backup.reprogram();
        game.getTeam().add(backup);
        starter.takeDamage(999);

        game.onCombatEnd(victoryAgainst(new HealerBot(2)));

        RepairingState state = assertInstanceOf(RepairingState.class, starter.getState());
        assertEquals(RepairingState.VICTORIES_TO_REPAIR, state.getVictoriesLeft());
    }

    @Test
    void beatingAZoneChief_neutralizesItsPilot() {
        Game game = Game.newGame();
        game.onCombatEnd(victoryAgainst(new Exosquelette(4)));
        assertEquals(10, game.getWantedBar().getValue());
    }

    @Test
    void beatingARobot_leavesTheWantedBar_untilTheChoice() {
        Game game = Game.newGame();
        game.onCombatEnd(victoryAgainst(new HealerBot(2)));
        assertEquals(0, game.getWantedBar().getValue());
    }

    @Test
    void theConsequences_listenToTheGameEvents() {
        Game game = Game.newGame();
        game.getEvents().publish(new RobotKilledEvent(new HealerBot(3)));
        assertEquals(15, game.getWantedBar().getValue());
        assertEquals(1, game.getDataCenter().getRobotsKilled());
        assertEquals(2, game.getParts().getParts());
    }

    @Test
    void noCombatIsRecorded_afterTheEnd() {
        Game game = Game.newGame();
        game.onCombatEnd(victoryAgainst(new Boss()));
        assertThrows(IllegalStateException.class, () -> game.onCombatEnd(victoryAgainst(new HealerBot(1))));
    }
}
