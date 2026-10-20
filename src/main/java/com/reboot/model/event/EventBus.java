package com.reboot.model.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Le sujet du pattern Observer : il relaie chaque événement publié à tous ses abonnés.
 *
 * <p>Celui qui publie (l'écran de choix, le combat) ne connaît pas ceux qui réagissent
 * (barre de recherche, réseau, pièces) : on ajoute une conséquence en abonnant un listener,
 * sans modifier le code qui publie.
 */
public class EventBus {

    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * Abonne un listener à tous les événements à venir.
     *
     * @param listener le listener à abonner
     */
    public void subscribe(GameEventListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Prévient chaque abonné, dans l'ordre d'abonnement.
     *
     * @param event l'événement à publier
     */
    public void publish(GameEvent event) {
        Objects.requireNonNull(event, "event");
        for (GameEventListener listener : listeners) {
            event.dispatchTo(listener);
        }
    }
}
