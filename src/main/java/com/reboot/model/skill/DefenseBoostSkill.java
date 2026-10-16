package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

public class DefenseBoostSkill implements Skill {

    private final String name;
    private final int multiplier;
    private final int turns;

    public DefenseBoostSkill(String name, int multiplier, int turns) {
        if (multiplier < 2 || turns < 1) {
            throw new IllegalArgumentException("A boost needs a multiplier of at least 2 and one turn");
        }
        this.name = name;
        this.multiplier = multiplier;
        this.turns = turns;
    }

    @Override
    public String apply(Combattant user, Combattant target) {
        user.boostDefense(multiplier, turns);
        return user.getName() + " utilise " + name + " : DEF x" + multiplier + " pendant " + turns + " tours.";
    }

    @Override
    public String getName() { return name; }

    public int getMultiplier() { return multiplier; }
    public int getTurns() { return turns; }
}
