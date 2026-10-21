package com.reboot.model.team;

import com.reboot.model.fighter.Robot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Team {

    public static final int MAX_SIZE = 3;

    private final List<Robot> robots = new ArrayList<>();

    public Team(Robot starter) {
        add(starter);
    }

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

    public boolean isFull() {
        return robots.size() >= MAX_SIZE;
    }

    public List<Robot> getRobots() {
        return Collections.unmodifiableList(robots);
    }

    /** @return le niveau d'équipe : la somme des niveaux de ses robots */
    public int getTeamLevel() {
        return robots.stream().mapToInt(Robot::getLevel).sum();
    }

    /** @return le robot de plus bas niveau de l'équipe */
    public Robot getWeakest() {
        return robots.stream().min(Comparator.comparingInt(Robot::getLevel)).orElseThrow();
    }

    public boolean hasFighterLeft() {
        return robots.stream().anyMatch(Team::canFight);
    }

    public Robot getActive() {
        return robots.stream().filter(Team::canFight).findFirst()
                .orElseThrow(() -> new IllegalStateException("No robot can fight"));
    }

    public boolean isAllUnderRepair() {
        return robots.stream().allMatch(Robot::isUnderRepair);
    }

    public void recordVictory() {
        robots.forEach(Robot::recordTeamVictory);
    }

    public void sendKnockedOutToRepair() {
        robots.stream().filter(robot -> robot.canFightForPlayer() && robot.isKo()).forEach(Robot::handleKnockOut);
    }

    public void healAll() {
        robots.stream().filter(Robot::canFightForPlayer).forEach(robot -> robot.heal(robot.getMaxHp()));
    }

    private static boolean canFight(Robot robot) {
        return robot.canFightForPlayer() && !robot.isKo();
    }
}
