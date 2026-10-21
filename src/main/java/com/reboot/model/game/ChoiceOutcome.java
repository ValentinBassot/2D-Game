package com.reboot.model.game;

import java.util.List;

/**
 * Ce qui s'est passé après le choix, pour l'afficher au joueur.
 *
 * @param reprogrammed {@code true} si le robot a été reprogrammé, {@code false} s'il a été achevé
 * @param lines les conséquences, une par ligne
 */
public record ChoiceOutcome(boolean reprogrammed, List<String> lines) {

    /**
     * Copie la liste pour que le résultat ne puisse plus changer.
     *
     * @param reprogrammed le choix fait
     * @param lines les conséquences
     */
    public ChoiceOutcome {
        lines = List.copyOf(lines);
    }
}
