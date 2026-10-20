package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Robot;

public final class DamageCalculator {

    private DamageCalculator() {
    }

    public static int compute(Combattant attacker, Combattant defender, int power, boolean ignoresDefense) {
        int base = power + attacker.getAtk();
        if (!ignoresDefense) {
            base -= defender.getDef();
        }
        double damage = base * multiplier(attacker, defender);
        return Math.max(1, (int) Math.floor(damage));
    }

    private static double multiplier(Combattant attacker, Combattant defender) {
        if (attacker instanceof Robot a && defender instanceof Robot d) {
            return a.getType().multiplierAgainst(d.getType());
        }
        return 1.0;
    }
}