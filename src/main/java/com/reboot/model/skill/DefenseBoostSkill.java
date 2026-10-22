package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

/** Compétence qui multiplie la défense du lanceur pendant quelques tours. */
public class DefenseBoostSkill implements Skill {

    private final String name;
    private final int multiplier;
    private final int turns;

    /**
     * Crée un bonus de défense.
     *
     * @param name son nom
     * @param multiplier le multiplicateur, au moins 2
     * @param turns la durée en tours, au moins 1
     */
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

    /** {@return le multiplicateur de défense} */
    public int getMultiplier() { return multiplier; }
    /** {@return la durée du bonus, en tours} */
    public int getTurns() { return turns; }
}
