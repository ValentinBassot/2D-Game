package com.reboot.model.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bus d'événements, c'est le sujet de notre pattern Observer. Il transmet chaque événement à tous ses
 * abonnés.
 *
 * <p>Le code qui publie ne connaît pas ceux qui réagissent. Pour ajouter une conséquence, on abonne un
 * nouveau listener et on ne touche pas au reste.
 */
public class EventBus {

    private final List<GameEventListener> listeners = new ArrayList<>();

    /** Crée un bus vide, sans abonné. */
    public EventBus() {
    }

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
