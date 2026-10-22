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
 * Une partie en cours, avec l'équipe, la Home, les conséquences des choix et l'avancée dans les zones. Les
 * quêtes de la {@link Campaign} lisent leur progression ici.
 */
public class Game implements QuestContext {

    /** Le niveau du robot de départ de Léon. */
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

    /**
     * Crée une partie avec une équipe, et abonne les conséquences au bus d'événements.
     *
     * @param team l'équipe du joueur
     */
    public Game(Team team) {
        this.team = team;
        events.subscribe(wantedBar);
        events.subscribe(dataCenter);
        events.subscribe(parts);
    }

    /** {@return une nouvelle partie, avec l'InfighterBot réparé par Léon comme seul robot} */
    public static Game newGame() {
        InfighterBot starter = new InfighterBot(STARTER_LEVEL);
        starter.reprogram();
        return new Game(new Team(starter));
    }

    /**
     * Met à jour la partie après un combat. On compte la victoire dans la zone, on avance les réparations,
     * on envoie les robots K.O. en réparation et on regarde si la partie est finie.
     *
     * @param result le résultat de la rencontre
     * @throws IllegalStateException si la partie est déjà finie
     */
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
     * À appeler quand le joueur entre dans une zone, pour y compter ses victoires. La première entrée au
     * data center fixe aussi le nombre de gardes, selon la barre de recherche.
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
     * {@return {@code true} si son portail est allumé}
     *
     * @param zone le numéro d'une zone
     */
    public boolean isZoneUnlocked(int zone) {
        return campaign.isUnlocked(zone, this);
    }

    /**
     * {@return {@code true} si son chef accepte le combat}
     *
     * @param zone le quartier ou la zone industrielle
     */
    public boolean canChallengeChief(int zone) {
        return !isChiefDefeated(zone) && campaign.canChallengeChief(zone, this);
    }

    /** {@return {@code true} si le chef de NEXUS accepte le combat} */
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
     * Retour à la Home. Les robots rangés remplacent ceux qui sont en réparation, puis toute l'équipe est
     * soignée.
     */
    public void restAtHome() {
        home.fillTeam(team);
        team.healAll();
    }

    /** {@return {@code true} si la partie est gagnée ou perdue} */
    public boolean isOver() {
        return status != GameStatus.IN_PROGRESS;
    }

    /** {@return l'équipe du joueur} */
    public Team getTeam() { return team; }
    /** {@return la campagne et ses quêtes} */
    public Campaign getCampaign() { return campaign; }
    /** {@return la zone où se trouve le joueur} */
    public int getCurrentZone() { return currentZone; }
    /** {@return la Home et ses robots rangés} */
    public Home getHome() { return home; }
    /** {@return le bus des événements du jeu} */
    public EventBus getEvents() { return events; }
    /** {@return la barre de recherche} */
    public WantedBar getWantedBar() { return wantedBar; }
    /** {@return le réseau du data center} */
    public DataCenter getDataCenter() { return dataCenter; }
    /** {@return le stock de pièces} */
    public PartsStock getParts() { return parts; }
    /** {@return l'état de la partie} */
    public GameStatus getStatus() { return status; }
}
