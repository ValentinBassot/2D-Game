package com.reboot.model.event;

/**
 * Événement publié sur l'{@link EventBus} quand une action change le monde.
 *
 * <p>Chaque événement appelle lui-même la bonne méthode du listener (double dispatch). Le bus n'a donc pas
 * à tester le type de l'événement.
 */
public interface GameEvent {

    /**
     * Transmet l'événement à la méthode du listener qui lui correspond.
     *
     * @param listener le listener à prévenir
     */
    void dispatchTo(GameEventListener listener);
}
