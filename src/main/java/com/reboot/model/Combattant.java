package com.reboot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Combattant {

    private final String name;
    private final int level;
    private final int maxHp;
    private int hp;
    private final int atk;
    private final int def;
    private final int speed;
    private final List<Skill> skills = new ArrayList<>();

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
    public int getDef() { return def; }
    public int getSpeed() { return speed; }
}