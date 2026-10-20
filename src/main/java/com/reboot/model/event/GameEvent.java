package com.reboot.model.event;

/**
 * Un événement publié sur l'{@link EventBus} quand une action change le monde.
 *
 * <p>Chaque événement sait quelle méthode du listener appeler (double dispatch) :
 * le bus n'a donc jamais besoin de tester le type de l'événement.
 */
public interface GameEvent {

    /**
     * Transmet l'événement à la méthode du listener qui lui correspond.
     *
     * @param listener le listener à prévenir
     */
    void dispatchTo(GameEventListener listener);
}
