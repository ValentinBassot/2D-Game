package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Combattant {

    public static final DamageSkill STRIKE = new DamageSkill("Frappe", 10);

    private final String name;
    private final int level;
    private final int maxHp;
    private int hp;
    private final int atk;
    private final int def;
    private final int speed;
    private final List<Skill> skills = new ArrayList<>();
    private int defenseMultiplier = 1;
    private int boostTurnsLeft;

    protected Combattant(String name, int level, int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        this.name = name;
        this.level = level;
        this.maxHp = statAtLevel(baseHp, level);
        this.hp = this.maxHp;
        this.atk = statAtLevel(baseAtk, level);
        this.def = statAtLevel(baseDef, level);
        this.speed = statAtLevel(baseSpeed, level);
    }

    private static int statAtLevel(int base, int level) {
        return base + base * (level - 1) / 10;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        this.hp = Math.max(0, this.hp - amount);
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal cannot be negative");
        }
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    public boolean isKo() {
        return this.hp == 0;
    }

    public abstract boolean isReprogrammable();

    public boolean endsTheGame() {
        return false;
    }

    public double advantageAgainst(Combattant defender) {
        return 1.0;
    }

    protected double weaknessTo(RobotType attackerType) {
        return 1.0;
    }

    public void boostDefense(int multiplier, int turns) {
        if (multiplier < 1 || turns < 1) {
            throw new IllegalArgumentException("Invalid defense boost");
        }
        this.defenseMultiplier = multiplier;
        this.boostTurnsLeft = turns;
    }

    public void endTurn() {
        if (boostTurnsLeft > 0) {
            boostTurnsLeft--;
            if (boostTurnsLeft == 0) {
                defenseMultiplier = 1;
            }
        }
    }

    protected final void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def * defenseMultiplier; }
    public int getSpeed() { return speed; }
}