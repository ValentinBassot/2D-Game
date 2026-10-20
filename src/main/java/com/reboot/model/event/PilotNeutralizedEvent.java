package com.reboot.model.event;

import com.reboot.model.fighter.Combattant;

/**
 * Le joueur a vaincu un exosquelette : son pilote humain est neutralisé.
 *
 * @param exosquelette l'exosquelette vaincu
 */
public record PilotNeutralizedEvent(Combattant exosquelette) implements GameEvent {

    @Override
    public void dispatchTo(GameEventListener listener) {
        listener.onPilotNeutralized(this);
    }
}
