package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

/** Compétence d'attaque, qui inflige des dégâts. */
public class DamageSkill implements Skill {

    private final String name;
    private final int power;
    private final boolean ignoresDefense;
    private final boolean priority;

    /**
     * Crée une attaque normale, sans priorité, qui prend en compte la défense.
     *
     * @param name son nom
     * @param power sa puissance
     */
    public DamageSkill(String name, int power) {
        this(name, power, false, false);
    }

    /**
     * Crée une attaque.
     *
     * @param name son nom
     * @param power sa puissance, positive ou nulle
     * @param ignoresDefense {@code true} si elle ignore la défense
     * @param priority {@code true} si elle joue toujours en premier
     */
    public DamageSkill(String name, int power, boolean ignoresDefense, boolean priority) {
        if (power < 0) {
            throw new IllegalArgumentException("Power cannot be negative");
        }
        this.name = name;
        this.power = power;
        this.ignoresDefense = ignoresDefense;
        this.priority = priority;
    }

    @Override
    public String apply(Combattant user, Combattant target) {
        int damage = DamageCalculator.compute(user, target, power, ignoresDefense);
        target.takeDamage(damage);
        return user.getName() + " utilise " + name + " : " + damage + " dégâts.";
    }

    @Override
    public String getName() { return name; }

    @Override
    public boolean hasPriority() { return priority; }

    /** {@return sa puissance} */
    public int getPower() { return power; }
    /** {@return {@code true} si elle ignore la défense} */
    public boolean ignoresDefense() { return ignoresDefense; }
}
