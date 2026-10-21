package com.reboot.model.game;

import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.event.RobotReprogrammedEvent;
import com.reboot.model.fighter.Robot;
import com.reboot.model.nexus.WantedBar;
import com.reboot.model.team.PartsStock;
import java.util.List;

/**
 * Applique le choix du joueur après une victoire contre un robot : le reprogrammer ou l'achever.
 *
 * <p>Le résolveur ne modifie ni la barre de recherche, ni le réseau, ni les pièces : il publie
 * un événement, et ce sont les abonnés du bus qui réagissent (pattern Observer).
 */
public class ChoiceResolver {

    private final Game game;

    /**
     * @param game la partie dont l'équipe, la Home et le bus d'événements sont utilisés
     */
    public ChoiceResolver(Game game) {
        this.game = game;
    }

    /**
     * Décrit ce que coûtera la reprogrammation, avant que le joueur ne choisisse.
     *
     * @param robot le robot vaincu
     * @return les conséquences, sur une ligne
     */
    public String previewReprogram(Robot robot) {
        String destination;
        if (!game.getTeam().isFull()) {
            destination = "rejoint l'équipe";
        } else if (replacesTheWeakest(robot)) {
            destination = "remplace " + game.getTeam().getWeakest().getName() + " (Nv "
                    + game.getTeam().getWeakest().getLevel() + ")";
        } else {
            destination = "part à la Home";
        }
        return "- " + WantedBar.REPROGRAM + " recherche · " + destination;
    }

    /**
     * Décrit ce que coûtera l'élimination, avant que le joueur ne choisisse.
     *
     * @param robot le robot vaincu
     * @return les conséquences, sur une ligne
     */
    public String previewFinish(Robot robot) {
        return "+ " + WantedBar.KILL + " recherche · + " + PartsStock.partsFor(robot) + " pièces · réseau + 1";
    }

    /**
     * Reprogramme le robot : il redevient bienveillant, est remis en état et rejoint l'équipe.
     * Si l'équipe est pleine, il remplace le robot le plus faible quand il est plus fort que lui
     * (le remplacé part à la Home), sinon il part lui-même à la Home.
     *
     * @param robot le robot vaincu, encore détourné
     * @return les conséquences à afficher
     */
    public ChoiceOutcome reprogram(Robot robot) {
        requireHijacked(robot);
        int wantedBefore = game.getWantedBar().getValue();
        robot.reprogram();
        robot.heal(robot.getMaxHp());
        String destination;
        if (game.getTeam().add(robot)) {
            destination = robot.getName() + " rejoint ton équipe.";
        } else if (replacesTheWeakest(robot)) {
            Robot weakest = game.getTeam().getWeakest();
            game.getTeam().replace(weakest, robot);
            game.getHome().store(weakest);
            destination = robot.getName() + " remplace " + weakest.getName() + " (Nv " + weakest.getLevel()
                    + "), qui part à la Home.";
        } else {
            game.getHome().store(robot);
            destination = robot.getName() + " part à la Home : ton équipe est pleine.";
        }
        game.getEvents().publish(new RobotReprogrammedEvent(robot));
        return new ChoiceOutcome(true, List.of(
                destination,
                "Barre de recherche : " + wantedBefore + " -> " + game.getWantedBar().getValue()));
    }

    /**
     * Achève le robot : il est détruit, le joueur récupère des pièces et NEXUS s'en souvient.
     *
     * @param robot le robot vaincu, encore détourné
     * @return les conséquences à afficher
     */
    public ChoiceOutcome finish(Robot robot) {
        requireHijacked(robot);
        int wantedBefore = game.getWantedBar().getValue();
        int partsBefore = game.getParts().getParts();
        game.getEvents().publish(new RobotKilledEvent(robot));
        int gained = game.getParts().getParts() - partsBefore;
        return new ChoiceOutcome(false, List.of(
                robot.getName() + " est détruit. Tu récupères " + gained + " pièce(s), total "
                        + game.getParts().getParts() + ".",
                "Barre de recherche : " + wantedBefore + " -> " + game.getWantedBar().getValue(),
                "Le réseau NEXUS a enregistré sa défaite... (robots achevés : "
                        + game.getDataCenter().getRobotsKilled() + ")"));
    }

    private boolean replacesTheWeakest(Robot recruit) {
        return recruit.getLevel() > game.getTeam().getWeakest().getLevel();
    }

    private static void requireHijacked(Robot robot) {
        if (!robot.isReprogrammable()) {
            throw new IllegalStateException(robot.getName() + " is not a hijacked robot");
        }
    }
}
