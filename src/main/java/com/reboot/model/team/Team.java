package com.reboot.model.team;

import com.reboot.model.fighter.Robot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Équipe du joueur, {@value #MAX_SIZE} robots alliés maximum. Le robot actif est le premier encore en état
 * de se battre.
 */
public class Team {

    /** Le nombre maximal de robots dans l'équipe. */
    public static final int MAX_SIZE = 3;

    private final List<Robot> robots = new ArrayList<>();

    /**
     * Crée une équipe avec son premier robot.
     *
     * @param starter un robot déjà reprogrammé
     */
    public Team(Robot starter) {
        add(starter);
    }

    /**
     * Ajoute un robot allié.
     *
     * @param robot un robot reprogrammé, qui n'est pas déjà dans l'équipe
     * @return {@code false} si l'équipe est pleine
     */
    public boolean add(Robot robot) {
        if (robot.isReprogrammable()) {
            throw new IllegalArgumentException("A hijacked robot must be reprogrammed before joining the team");
        }
        if (robots.contains(robot)) {
            throw new IllegalArgumentException(robot.getName() + " is already in the team");
        }
        if (isFull()) {
            return false;
        }
        robots.add(robot);
        return true;
    }

    /**
     * Remplace un robot de l'équipe par un autre, à la même place.
     *
     * @param out le robot qui quitte l'équipe
     * @param in le robot qui le remplace
     */
    public void replace(Robot out, Robot in) {
        int index = robots.indexOf(out);
        if (index < 0) {
            throw new IllegalArgumentException(out.getName() + " is not in the team");
        }
        if (in.isReprogrammable() || robots.contains(in)) {
            throw new IllegalArgumentException(in.getName() + " cannot join the team");
        }
        robots.set(index, in);
    }

    /** {@return {@code true} si l'équipe a {@value #MAX_SIZE} robots} */
    public boolean isFull() {
        return robots.size() >= MAX_SIZE;
    }

    /** {@return ses robots, en lecture seule} */
    public List<Robot> getRobots() {
        return Collections.unmodifiableList(robots);
    }

    /** {@return le niveau d'équipe, c'est-à-dire la somme des niveaux des robots} */
    public int getTeamLevel() {
        return robots.stream().mapToInt(Robot::getLevel).sum();
    }

    /** {@return le robot de plus bas niveau de l'équipe} */
    public Robot getWeakest() {
        return robots.stream().min(Comparator.comparingInt(Robot::getLevel)).orElseThrow();
    }

    /** {@return {@code true} si au moins un robot peut se battre} */
    public boolean hasFighterLeft() {
        return robots.stream().anyMatch(Team::canFight);
    }

    /**
     * {@return le premier robot qui peut se battre}
     *
     * @throws IllegalStateException si aucun ne le peut
     */
    public Robot getActive() {
        return robots.stream().filter(Team::canFight).findFirst()
                .orElseThrow(() -> new IllegalStateException("No robot can fight"));
    }

    /** {@return {@code true} si tous ses robots sont en réparation} */
    public boolean isAllUnderRepair() {
        return robots.stream().allMatch(Robot::isUnderRepair);
    }

    /** Signale une victoire à chaque robot, pour faire avancer les réparations. */
    public void recordVictory() {
        robots.forEach(Robot::recordTeamVictory);
    }

    /** Envoie en réparation les robots alliés mis K.O. */
    public void sendKnockedOutToRepair() {
        robots.stream().filter(robot -> robot.canFightForPlayer() && robot.isKo()).forEach(Robot::handleKnockOut);
    }

    /** Soigne complètement les robots qui ne sont pas en réparation. */
    public void healAll() {
        robots.stream().filter(Robot::canFightForPlayer).forEach(robot -> robot.heal(robot.getMaxHp()));
    }

    private static boolean canFight(Robot robot) {
        return robot.canFightForPlayer() && !robot.isKo();
    }
}
