package com.reboot.model.combat;

import java.util.List;

/**
 * Ce qui s'est passé pendant un tour.
 *
 * @param log le journal du tour, une action par ligne
 * @param combatOver {@code true} si le combat est terminé
 */
public record TurnResult(List<String> log, boolean combatOver) {

    /**
     * Copie le journal pour qu'il ne puisse plus changer.
     *
     * @param log le journal du tour
     * @param combatOver {@code true} si le combat est terminé
     */
    public TurnResult {
        log = List.copyOf(log);
    }
}