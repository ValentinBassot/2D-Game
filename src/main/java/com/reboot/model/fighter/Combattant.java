package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tout ce qui se bat : stats, points de vie, compétences et expérience.
 *
 * <p>Les stats se calculent à partir des stats de base et du niveau : chaque niveau ajoute
 * 10 % de la valeur de base. Monter de niveau fait donc grandir toutes les stats.
 */
public abstract class Combattant {

    /** La frappe de base, connue de tous les combattants. */
    public static final DamageSkill STRIKE = new DamageSkill("Frappe", 10);
    /** L'XP nécessaire pour passer au niveau suivant vaut ce nombre fois le niveau actuel. */
    public static final int XP_PER_LEVEL = 50;
    /** L'XP rapportée par un combattant vaincu vaut ce nombre fois son niveau. */
    public static final int XP_REWARD_PER_LEVEL = 10;

    private final String name;
    private final int baseHp;
    private final int baseAtk;
    private final int baseDef;
    private final int baseSpeed;
    private int level;
    private int xp;
    private int hp;
    private final List<Skill> skills = new ArrayList<>();
    private int defenseMultiplier = 1;
    private int boostTurnsLeft;

    protected Combattant(String name, int level, int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        this.name = name;
        this.level = level;
        this.baseHp = baseHp;
        this.baseAtk = baseAtk;
        this.baseDef = baseDef;
        this.baseSpeed = baseSpeed;
        this.hp = getMaxHp();
    }

    private int statAtLevel(int base) {
        return base + base * (level - 1) / 10;
    }

    /**
     * Ajoute de l'expérience et fait monter de niveau autant de fois que possible.
     * Les PV gagnés en montant de niveau s'ajoutent aux PV actuels, sauf pour un combattant K.O.
     *
     * @param amount l'XP gagnée, positive ou nulle
     * @return le nombre de niveaux gagnés
     */
    public int gainXp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP cannot be negative");
        }
        xp += amount;
        int gained = 0;
        while (xp >= getXpToNextLevel()) {
            xp -= getXpToNextLevel();
            int maxBefore = getMaxHp();
            level++;
            gained++;
            if (!isKo()) {
                hp += getMaxHp() - maxBefore;
            }
        }
        return gained;
    }

    /** @return l'XP nécessaire pour passer au niveau suivant */
    public int getXpToNextLevel() {
        return XP_PER_LEVEL * level;
    }

    /** @return l'XP que rapporte ce combattant quand il est vaincu */
    public int getXpReward() {
        return XP_REWARD_PER_LEVEL * level;
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
        this.hp = Math.min(getMaxHp(), this.hp + amount);
    }

    public boolean isKo() {
        return this.hp == 0;
    }

    public abstract boolean isReprogrammable();

    public abstract String getSpriteId();

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
    public int getXp() { return xp; }
    public int getHp() { return hp; }
    public int getMaxHp() { return statAtLevel(baseHp); }
    public int getAtk() { return statAtLevel(baseAtk); }
    public int getDef() { return statAtLevel(baseDef) * defenseMultiplier; }
    public int getSpeed() { return statAtLevel(baseSpeed); }
}