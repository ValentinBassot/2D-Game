package com.reboot.model.game;

import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.EncounterResult;
import com.reboot.model.event.EventBus;
import com.reboot.model.event.PilotNeutralizedEvent;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.nexus.DataCenter;
import com.reboot.model.nexus.WantedBar;
import com.reboot.model.team.Home;
import com.reboot.model.team.PartsStock;
import com.reboot.model.team.Team;

public class Game {

    public static final int STARTER_LEVEL = 2;

    private final Team team;
    private final Home home = new Home();
    private final EventBus events = new EventBus();
    private final WantedBar wantedBar = new WantedBar();
    private final DataCenter dataCenter = new DataCenter();
    private final PartsStock parts = new PartsStock();
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(Team team) {
        this.team = team;
        events.subscribe(wantedBar);
        events.subscribe(dataCenter);
        events.subscribe(parts);
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
            } else if (!result.enemy().isReprogrammable()) {
                events.publish(new PilotNeutralizedEvent(result.enemy()));
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
    public Home getHome() { return home; }
    public EventBus getEvents() { return events; }
    public WantedBar getWantedBar() { return wantedBar; }
    public DataCenter getDataCenter() { return dataCenter; }
    public PartsStock getParts() { return parts; }
    public GameStatus getStatus() { return status; }
}
