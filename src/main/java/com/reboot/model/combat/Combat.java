package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Robot;
import com.reboot.model.skill.Skill;
import com.reboot.model.team.Team;
import java.util.ArrayList;
import java.util.List;

/**
 * Un combat au tour par tour entre le robot actif du joueur et un ennemi.
 *
 * <p>Quand le joueur se bat avec une équipe, il peut changer de robot : le changement coûte
 * le tour, sauf si le robot actif vient d'être mis K.O. Le combat est perdu quand plus aucun
 * robot de l'équipe ne peut se battre.
 */
public class Combat {

    private final List<Robot> roster;
    private final Combattant enemy;
    private Combattant active;
    private int turn;

    /**
     * Un combat en solo, sans changement de robot possible.
     *
     * @param player le combattant du joueur
     * @param enemy l'ennemi
     */
    public Combat(Combattant player, Combattant enemy) {
        this(List.of(), player, enemy);
    }

    /**
     * Un combat en équipe : le premier robot disponible commence.
     *
     * @param team l'équipe du joueur
     * @param enemy l'ennemi
     */
    public Combat(Team team, Combattant enemy) {
        this(team.getRobots(), team.getActive(), enemy);
    }

    private Combat(List<Robot> roster, Combattant active, Combattant enemy) {
        this.roster = roster;
        this.active = active;
        this.enemy = enemy;
    }

    /**
     * Joue un tour : le robot actif utilise une compétence, l'ennemi répond.
     *
     * @param skill une compétence du robot actif
     * @return le journal du tour
     */
    public TurnResult useSkill(Skill skill) {
        requireRunning();
        if (mustSwitch()) {
            throw new IllegalStateException(active.getName() + " is knocked out, choose another robot");
        }
        if (!active.getSkills().contains(skill)) {
            throw new IllegalArgumentException(active.getName() + " does not know " + skill.getName());
        }
        turn++;
        List<String> log = new ArrayList<>();
        Skill enemySkill = chooseEnemySkill();

        boolean playerFirst = playerActsFirst(skill, enemySkill);
        Combattant first = playerFirst ? active : enemy;
        Combattant second = playerFirst ? enemy : active;
        Skill firstSkill = playerFirst ? skill : enemySkill;
        Skill secondSkill = playerFirst ? enemySkill : skill;

        act(first, firstSkill, second, log);
        if (!second.isKo()) {
            act(second, secondSkill, first, log);
        }
        active.endTurn();
        enemy.endTurn();
        return new TurnResult(log, isOver());
    }

    /**
     * Envoie un autre robot de l'équipe au combat. Si le robot actif est encore debout,
     * le changement coûte le tour et l'ennemi attaque le nouveau venu ; s'il est K.O.,
     * le remplacement est gratuit.
     *
     * @param next un robot de {@link #getSwitchOptions()}
     * @return le journal du tour
     */
    public TurnResult switchTo(Robot next) {
        requireRunning();
        if (!getSwitchOptions().contains(next)) {
            throw new IllegalArgumentException(next.getName() + " cannot enter the combat");
        }
        List<String> log = new ArrayList<>();
        if (mustSwitch()) {
            active = next;
            log.add(next.getName() + " entre en combat !");
            return new TurnResult(log, false);
        }
        turn++;
        log.add(active.getName() + " revient. " + next.getName() + " entre en combat !");
        active = next;
        act(enemy, chooseEnemySkill(), active, log);
        active.endTurn();
        enemy.endTurn();
        return new TurnResult(log, isOver());
    }

    /** @return les robots de l'équipe qui peuvent remplacer le robot actif */
    public List<Robot> getSwitchOptions() {
        return roster.stream()
                .filter(robot -> robot != active && robot.canFightForPlayer() && !robot.isKo())
                .toList();
    }

    /** @return {@code true} si le robot actif est K.O. et qu'il faut choisir son remplaçant */
    public boolean mustSwitch() {
        return active.isKo() && !enemy.isKo() && !getSwitchOptions().isEmpty();
    }

    private void requireRunning() {
        if (isOver()) {
            throw new IllegalStateException("The combat is already over");
        }
    }

    private boolean playerActsFirst(Skill playerSkill, Skill enemySkill) {
        if (playerSkill.hasPriority() != enemySkill.hasPriority()) {
            return playerSkill.hasPriority();
        }
        return active.getSpeed() >= enemy.getSpeed();
    }

    private Skill chooseEnemySkill() {
        return enemy.getSkills().isEmpty() ? Combattant.STRIKE : enemy.getSkills().get(0);
    }

    private void act(Combattant attacker, Skill skill, Combattant defender, List<String> log) {
        log.add(skill.apply(attacker, defender));
        if (defender.isKo()) {
            log.add(defender.getName() + " est K.O. !");
        }
    }

    /** @return {@code true} si l'ennemi est K.O. ou si plus aucun robot du joueur ne peut se battre */
    public boolean isOver() {
        return enemy.isKo() || (active.isKo() && getSwitchOptions().isEmpty());
    }

    /** @return la victoire si l'ennemi est K.O., la défaite sinon */
    public CombatOutcome getOutcome() {
        if (!isOver()) {
            throw new IllegalStateException("The combat is not over yet");
        }
        return enemy.isKo() ? CombatOutcome.VICTORY : CombatOutcome.DEFEAT;
    }

    /** @return le nombre de tours joués */
    public int getTurn() { return turn; }

    /** @return le combattant actif du joueur */
    public Combattant getPlayer() { return active; }

    /** @return l'ennemi */
    public Combattant getEnemy() { return enemy; }
}
