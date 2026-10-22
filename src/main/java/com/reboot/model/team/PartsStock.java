package com.reboot.model.team;

import com.reboot.model.event.GameEventListener;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.fighter.Robot;

/**
 * Les pièces de réparation du joueur. On ne les obtient qu'en achevant des robots,
 * et chacune retire une victoire au délai de réparation d'un robot K.O.
 */
public class PartsStock implements GameEventListener {

    private int parts;

    /**
     * Le nombre de pièces récupérées en achevant un robot : plus il est haut niveau, plus il en donne.
     *
     * @param robot le robot achevé
     * @return 1 pièce, plus 1 tous les 3 niveaux
     */
    public static int partsFor(Robot robot) {
        return 1 + robot.getLevel() / 3;
    }

    @Override
    public void onRobotKilled(RobotKilledEvent event) {
        parts += partsFor(event.robot());
    }

    /**
     * Dépense une pièce pour avancer la réparation d'un robot.
     *
     * @param robot le robot à réparer
     * @return {@code true} si une pièce a été utilisée, {@code false} s'il n'y a plus de pièce
     *         ou si le robot n'est pas en réparation
     */
    public boolean repair(Robot robot) {
        if (parts == 0 || !robot.isUnderRepair()) {
            return false;
        }
        parts--;
        robot.useRepairPart();
        return true;
    }

    /** @return le nombre de pièces disponibles */
    public int getParts() { return parts; }
}
