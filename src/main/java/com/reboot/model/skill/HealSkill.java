package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

public class HealSkill implements Skill {

    private final String name;
    private final int amount;

    public HealSkill(String name, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Heal amount must be positive");
        }
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String apply(Combattant user, Combattant target) {
        int before = user.getHp();
        user.heal(amount);
        return user.getName() + " utilise " + name + " : + " + (user.getHp() - before) + " PV.";
    }

    @Override
    public String getName() { return name; }

    public int getAmount() { return amount; }
}
