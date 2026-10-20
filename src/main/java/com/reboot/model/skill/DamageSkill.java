package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

public class DamageSkill implements Skill {

    private final String name;
    private final int power;
    private final boolean ignoresDefense;
    private final boolean priority;

    public DamageSkill(String name, int power) {
        this(name, power, false, false);
    }

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

    public int getPower() { return power; }
    public boolean ignoresDefense() { return ignoresDefense; }
}
