package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;

/**
 * Le résultat d'une rencontre.
 *
 * @param outcome la victoire ou la défaite
 * @param enemy l'ennemi affronté
 * @param choiceAvailable {@code true} si le joueur peut reprogrammer ou achever l'ennemi
 */
public record EncounterResult(CombatOutcome outcome, Combattant enemy, boolean choiceAvailable) {
}
