package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.team.Team;

/**
 * Une rencontre, c'est un combat puis son résultat. Le résultat indique si le joueur peut reprogrammer ou
 * achever l'ennemi.
 */
public class Encounter {
    private final Combat combat;

    /**
     * Une rencontre en solo.
     *
     * @param player le combattant du joueur
     * @param enemy l'ennemi
     */
    public Encounter(Combattant player, Combattant enemy) {
        this.combat = new Combat(player, enemy);
    }

    /**
     * Une rencontre en équipe.
     *
     * @param team l'équipe du joueur
     * @param enemy l'ennemi
     * @param enemyStrategy le cerveau de l'ennemi
     */
    public Encounter(Team team, Combattant enemy, CombatStrategy enemyStrategy) {
        this.combat = new Combat(team, enemy, enemyStrategy);
    }

    /** {@return le combat de la rencontre} */
    public Combat getCombat() {
        return combat;
    }

    /** {@return {@code true} si le combat est terminé} */
    public boolean isFinished() {
        return combat.isOver();
    }

    /**
     * {@return le résultat de la rencontre}
     *
     * @throws IllegalStateException si le combat n'est pas terminé
     */
    public EncounterResult getResult() {
        if (!isFinished()) {
            throw new IllegalStateException("The encounter is not finished yet");
        }
        CombatOutcome outcome = combat.getOutcome();
        boolean choiceAvailable = outcome == CombatOutcome.VICTORY && combat.getEnemy().isReprogrammable();
        return new EncounterResult(outcome, combat.getEnemy(), choiceAvailable);
    }
}
