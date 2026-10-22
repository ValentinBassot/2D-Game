package com.reboot.model.game;

import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.EncounterResult;
import com.reboot.model.event.EventBus;
import com.reboot.model.event.PilotNeutralizedEvent;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.nexus.DataCenter;
import com.reboot.model.nexus.WantedBar;
import com.reboot.model.quest.Campaign;
import com.reboot.model.quest.QuestContext;
import com.reboot.model.team.Home;
import com.reboot.model.team.PartsStock;
import com.reboot.model.team.Team;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Une partie : l'équipe, la Home, les conséquences des choix et la progression dans les zones.
 * Elle sert de contexte aux quêtes de la {@link Campaign}.
 */
public class Game implements QuestContext {

    public static final int STARTER_LEVEL = 2;

    private final Team team;
    private final Home home = new Home();
    private final EventBus events = new EventBus();
    private final WantedBar wantedBar = new WantedBar();
    private final DataCenter dataCenter = new DataCenter();
    private final PartsStock parts = new PartsStock();
    private final Campaign campaign = new Campaign();
    private final Map<Integer, Integer> wins = new HashMap<>();
    private final Set<Integer> chiefsDefeated = new HashSet<>();
    private GameStatus status = GameStatus.IN_PROGRESS;
    private int currentZone = Campaign.HOME;
    private int guardsRequired;

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
            wins.merge(currentZone, 1, Integer::sum);
            team.recordVictory();
            home.recordVictory();
            team.sendKnockedOutToRepair();
            if (result.enemy().endsTheGame()) {
                status = GameStatus.WON;
            } else if (!result.enemy().isReprogrammable()) {
                chiefsDefeated.add(currentZone);
                events.publish(new PilotNeutralizedEvent(result.enemy()));
            }
        } else {
            team.sendKnockedOutToRepair();
            if (team.isAllUnderRepair() && home.hasNoFighter()) {
                status = GameStatus.LOST;
            }
        }
    }

    /**
     * Le joueur entre dans une zone : ses victoires y seront comptées. En entrant pour la première
     * fois au data center, la barre de recherche fixe le nombre de gardes à vaincre.
     *
     * @param zone le numéro de la zone
     */
    public void enterZone(int zone) {
        currentZone = zone;
        if (zone == Campaign.DATA_CENTER && guardsRequired == 0) {
            guardsRequired = Campaign.guardsFor(wantedBar.getValue());
        }
    }

    /**
     * @param zone le numéro d'une zone
     * @return {@code true} si son portail est allumé
     */
    public boolean isZoneUnlocked(int zone) {
        return campaign.isUnlocked(zone, this);
    }

    /**
     * @param zone le quartier ou la zone industrielle
     * @return {@code true} si son chef accepte le combat
     */
    public boolean canChallengeChief(int zone) {
        return !isChiefDefeated(zone) && campaign.canChallengeChief(zone, this);
    }

    /** @return {@code true} si le chef de NEXUS accepte le combat */
    public boolean canChallengeBoss() {
        return campaign.canChallengeBoss(this);
    }

    @Override
    public int getWins(int zone) {
        return wins.getOrDefault(zone, 0);
    }

    @Override
    public int getTeamSize() {
        return team.getRobots().size();
    }

    @Override
    public int getTeamLevel() {
        return team.getTeamLevel();
    }

    @Override
    public boolean isChiefDefeated(int zone) {
        return chiefsDefeated.contains(zone);
    }

    @Override
    public int getGuardsRequired() {
        return guardsRequired > 0 ? guardsRequired : Campaign.guardsFor(wantedBar.getValue());
    }

    @Override
    public boolean isBossDefeated() {
        return status == GameStatus.WON;
    }

    /**
     * Le joueur rentre à la Home : les robots rangés remplacent ceux en réparation,
     * puis l'équipe est soignée.
     */
    public void restAtHome() {
        home.fillTeam(team);
        team.healAll();
    }

    public boolean isOver() {
        return status != GameStatus.IN_PROGRESS;
    }

    public Team getTeam() { return team; }
    public Campaign getCampaign() { return campaign; }
    public int getCurrentZone() { return currentZone; }
    public Home getHome() { return home; }
    public EventBus getEvents() { return events; }
    public WantedBar getWantedBar() { return wantedBar; }
    public DataCenter getDataCenter() { return dataCenter; }
    public PartsStock getParts() { return parts; }
    public GameStatus getStatus() { return status; }
}
