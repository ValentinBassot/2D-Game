package com.reboot.model.fighter;

/**
 * État d'un robot (pattern State) : détourné, allié ou en réparation. Chaque transition renvoie le nouvel
 * état, comme ça {@link Robot} n'a pas besoin de tester son état avec des {@code if}.
 */
public interface RobotState {

    /** {@return {@code true} si le robot peut se battre pour le joueur} */
    boolean canFightForPlayer();

    /** {@return {@code true} si le robot peut être reprogrammé} */
    boolean isReprogrammable();

    /** {@return {@code true} si le robot est en réparation (faux par défaut)} */
    default boolean isUnderRepair() {
        return false;
    }

    /** {@return le nombre de victoires avant la fin de la réparation (0 par défaut)} */
    default int getVictoriesLeft() {
        return 0;
    }

    /** {@return l'état après une reprogrammation} */
    RobotState onReprogram();

    /** {@return l'état après un K.O.} */
    RobotState onKnockOut();

    /** {@return l'état après une victoire de l'équipe} */
    RobotState onTeamVictory();

    /** {@return l'état après une pièce dépensée} */
    RobotState onPartUsed();
}
