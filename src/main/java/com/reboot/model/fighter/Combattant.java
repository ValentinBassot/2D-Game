package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe mère de tout ce qui se bat. Elle gère les stats, les PV, les compétences et l'expérience.
 *
 * <p>Les stats viennent des stats de base et du niveau, avec +10 % de la base par niveau. Quand on monte
 * de niveau, toutes les stats augmentent.
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

    /**
     * Crée un combattant au niveau donné, avec tous ses PV.
     *
     * @param name son nom
     * @param level son niveau, au moins 1
     * @param baseHp ses PV au niveau 1
     * @param baseAtk son attaque au niveau 1
     * @param baseDef sa défense au niveau 1
     * @param baseSpeed sa vitesse au niveau 1
     */
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
     * Ajoute de l'XP et fait monter de niveau autant de fois que possible. Les PV gagnés à la montée de
     * niveau s'ajoutent aux PV actuels, sauf si le combattant est K.O.
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

    /** {@return l'XP nécessaire pour passer au niveau suivant} */
    public int getXpToNextLevel() {
        return XP_PER_LEVEL * level;
    }

    /** {@return l'XP que rapporte ce combattant quand il est vaincu} */
    public int getXpReward() {
        return XP_REWARD_PER_LEVEL * level;
    }

    /**
     * Retire des PV. On ne descend jamais sous 0.
     *
     * @param amount les dégâts subis, positifs ou nuls
     */
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        this.hp = Math.max(0, this.hp - amount);
    }

    /**
     * Rend des PV, dans la limite du maximum.
     *
     * @param amount les PV rendus, positifs ou nuls
     */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal cannot be negative");
        }
        this.hp = Math.min(getMaxHp(), this.hp + amount);
    }

    /** {@return {@code true} si le combattant n'a plus de PV} */
    public boolean isKo() {
        return this.hp == 0;
    }

    /** {@return {@code true} si le joueur peut reprogrammer ce combattant une fois vaincu} */
    public abstract boolean isReprogrammable();

    /** {@return le dossier de ses sprites, sous {@code assets/robots}} */
    public abstract String getSpriteId();

    /** {@return {@code true} si le vaincre termine la partie (seul le boss redéfinit cette méthode)} */
    public boolean endsTheGame() {
        return false;
    }

    /**
     * Premier appel du double dispatch pour le bonus de type. Par défaut un combattant sans type n'a aucun
     * avantage.
     *
     * @param defender le combattant attaqué
     * @return le multiplicateur de dégâts contre ce défenseur
     */
    public double advantageAgainst(Combattant defender) {
        return 1.0;
    }

    /**
     * Deuxième appel du double dispatch. Donne la faiblesse de ce combattant face au type de l'attaquant.
     *
     * @param attackerType le type de l'attaquant
     * @return le multiplicateur des dégâts subis
     */
    protected double weaknessTo(RobotType attackerType) {
        return 1.0;
    }

    /**
     * Multiplie la défense pendant quelques tours.
     *
     * @param multiplier le multiplicateur, au moins 1
     * @param turns le nombre de tours, au moins 1
     */
    public void boostDefense(int multiplier, int turns) {
        if (multiplier < 1 || turns < 1) {
            throw new IllegalArgumentException("Invalid defense boost");
        }
        this.defenseMultiplier = multiplier;
        this.boostTurnsLeft = turns;
    }

    /** Fin de tour. Le bonus de défense perd un tour et disparaît quand il arrive à 0. */
    public void endTurn() {
        if (boostTurnsLeft > 0) {
            boostTurnsLeft--;
            if (boostTurnsLeft == 0) {
                defenseMultiplier = 1;
            }
        }
    }

    /**
     * Ajoute une compétence. Réservé aux sous-classes, c'est elles qui choisissent leurs compétences.
     *
     * @param skill la compétence ajoutée
     */
    protected final void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /** {@return ses compétences, en lecture seule} */
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    /** {@return son nom} */
    public String getName() { return name; }
    /** {@return son niveau} */
    public int getLevel() { return level; }
    /** {@return l'XP accumulée depuis son dernier niveau} */
    public int getXp() { return xp; }
    /** {@return ses PV actuels} */
    public int getHp() { return hp; }
    /** {@return ses PV maximum à son niveau} */
    public int getMaxHp() { return statAtLevel(baseHp); }
    /** {@return son attaque à son niveau} */
    public int getAtk() { return statAtLevel(baseAtk); }
    /** {@return sa défense à son niveau, bonus de défense compris} */
    public int getDef() { return statAtLevel(baseDef) * defenseMultiplier; }
    /** {@return sa vitesse à son niveau, qui décide qui joue en premier} */
    public int getSpeed() { return statAtLevel(baseSpeed); }
}