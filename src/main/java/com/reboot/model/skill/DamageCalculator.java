package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

/**
 * Calcul des dégâts d'une attaque, avec la formule du GDD {@code max(1, puissance + ATK - DEF)}. On
 * multiplie ensuite par le bonus de type et on arrondit à l'entier inférieur.
 */
public final class DamageCalculator {

    private DamageCalculator() {
    }

    /**
     * {@return les dégâts infligés, au moins 1}
     *
     * @param attacker l'attaquant
     * @param defender le défenseur
     * @param power la puissance de la compétence
     * @param ignoresDefense {@code true} si la compétence ignore la défense
     */
    public static int compute(Combattant attacker, Combattant defender, int power, boolean ignoresDefense) {
        int base = power + attacker.getAtk();
        if (!ignoresDefense) {
            base -= defender.getDef();
        }
        double damage = base * attacker.advantageAgainst(defender);
        return Math.max(1, (int) Math.floor(damage));
    }
}