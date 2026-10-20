package com.reboot.model.game;

import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.EncounterResult;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.team.Team;

public class Game {

    public static final int STARTER_LEVEL = 2;

    private final Team team;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(Team team) {
        this.team = team;
    }

    public static Game newGame() {
        InfighterBot starter = new InfighterBot(STARTER_LEVEL);
        starter.reprogram();
        return new Game(new Team(starter));
    }

    public void onCombatEnd(EncounterResult result) {
        if (isOver()) {
            throw new IllegalStateException("The game is already over");
        }
        if (result.outcome() == CombatOutcome.VICTORY) {
            team.recordVictory();
            if (result.enemy().endsTheGame()) {
                status = GameStatus.WON;
            }
        } else {
            team.sendKnockedOutToRepair();
            if (team.isAllUnderRepair()) {
                status = GameStatus.LOST;
            }
        }
    }

    public boolean isOver() {
        return status != GameStatus.IN_PROGRESS;
    }

    public Team getTeam() { return team; }
    public GameStatus getStatus() { return status; }
}
