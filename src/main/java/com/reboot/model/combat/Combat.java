package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Robot;
import com.reboot.model.skill.Skill;
import java.util.ArrayList;
import java.util.List;

public class Combat {

    private final Combattant player;
    private final Combattant enemy;
    private int turn;

    public Combat(Combattant player, Combattant enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public TurnResult useSkill(Skill skill) {
        if (isOver()) {
            throw new IllegalStateException("The combat is already over");
        }
        if (!player.getSkills().contains(skill)) {
            throw new IllegalArgumentException(player.getName() + " does not know " + skill.getName());
        }
        turn++;
        List<String> log = new ArrayList<>();
        Skill enemySkill = chooseEnemySkill();

        boolean playerFirst = player.getSpeed() >= enemy.getSpeed();
        Combattant first = playerFirst ? player : enemy;
        Combattant second = playerFirst ? enemy : player;
        Skill firstSkill = playerFirst ? skill : enemySkill;
        Skill secondSkill = playerFirst ? enemySkill : skill;

        act(first, firstSkill, second, log);
        if (!second.isKo()) {
            act(second, secondSkill, first, log);
        }
        return new TurnResult(log, isOver());
    }

    private Skill chooseEnemySkill() {
        return enemy.getSkills().isEmpty() ? Robot.STRIKE : enemy.getSkills().get(0);
    }

    private void act(Combattant attacker, Skill skill, Combattant defender, List<String> log) {
        log.add(skill.apply(attacker, defender));
        if (defender.isKo()) {
            log.add(defender.getName() + " est K.O. !");
        }
    }

    public boolean isOver() {
        return player.isKo() || enemy.isKo();
    }

    public CombatOutcome getOutcome() {
        if (!isOver()) {
            throw new IllegalStateException("The combat is not over yet");
        }
        return enemy.isKo() ? CombatOutcome.VICTORY : CombatOutcome.DEFEAT;
    }

    public int getTurn() { return turn; }
    public Combattant getPlayer() { return player; }
    public Combattant getEnemy() { return enemy; }
}