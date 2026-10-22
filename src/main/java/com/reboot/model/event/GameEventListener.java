package com.reboot.model.event;

/**
 * Reçoit les événements du jeu. Par défaut les méthodes ne font rien, donc un listener redéfinit seulement
 * ce qui l'intéresse.
 */
public interface GameEventListener {

    /**
     * Appelée quand le joueur achève un robot.
     *
     * @param event l'événement reçu
     */
    default void onRobotKilled(RobotKilledEvent event) {
    }

    /**
     * Appelée quand le joueur reprogramme un robot.
     *
     * @param event l'événement reçu
     */
    default void onRobotReprogrammed(RobotReprogrammedEvent event) {
    }

    /**
     * Appelée quand le joueur neutralise le pilote d'un exosquelette.
     *
     * @param event l'événement reçu
     */
    default void onPilotNeutralized(PilotNeutralizedEvent event) {
    }
}
