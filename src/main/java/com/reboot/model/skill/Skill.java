package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

/**
 * Compétence de combat. {@link com.reboot.model.combat.Combat} l'utilise sans savoir de quelle classe elle
 * est, chaque compétence définit son propre effet dans {@link #apply}. C'est notre principal exemple de
 * polymorphisme.
 */
public interface Skill {

    /** {@return son nom, affiché dans le menu de combat} */
    String getName();

    /** {@return {@code true} si elle passe avant la vitesse (faux par défaut)} */
    default boolean hasPriority() {
        return false;
    }

    /** {@return {@code true} si elle ne sert qu'une fois par combat (faux par défaut)} */
    default boolean isOncePerCombat() {
        return false;
    }

    /**
     * Applique l'effet de la compétence.
     *
     * @param user celui qui l'utilise
     * @param target son adversaire
     * @return la ligne du journal qui décrit l'effet
     */
    String apply(Combattant user, Combattant target);
}
