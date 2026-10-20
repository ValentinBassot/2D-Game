package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

public final class DamageCalculator {

    private DamageCalculator() {
    }

    public static int compute(Combattant attacker, Combattant defender, int power, boolean ignoresDefense) {
        int base = power + attacker.getAtk();
        if (!ignoresDefense) {
            base -= defender.getDef();
        }
        double damage = base * attacker.advantageAgainst(defender);
        return Math.max(1, (int) Math.floor(damage));
    }
}