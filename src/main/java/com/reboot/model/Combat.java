package com.reboot.model;

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

        attack(first, firstSkill, second, log);
        if (!second.isKo()) {
            attack(second, secondSkill, first, log);
        }
        return new TurnResult(log, isOver());
    }

    private Skill chooseEnemySkill() {
        return enemy.getSkills().isEmpty() ? Robot.STRIKE : enemy.getSkills().get(0);
    }

    private void attack(Combattant attacker, Skill skill, Combattant defender, List<String> log) {
        int damage = DamageCalculator.compute(attacker, defender, skill.getPower(), skill.ignoresDefense());
        defender.takeDamage(damage);
        log.add(attacker.getName() + " utilise " + skill.getName() + " : " + damage + " dégâts.");
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