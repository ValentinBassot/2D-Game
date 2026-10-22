package com.reboot.model.nexus;

import com.reboot.model.event.GameEventListener;
import com.reboot.model.event.RobotKilledEvent;

/**
 * Le data center de NEXUS. Il compte les robots achevés, et plus il y en a, plus le réseau devient fort.
 */
public class DataCenter implements GameEventListener {

    /** Bonus de niveau maximum, pour que le jeu reste faisable. */
    public static final int MAX_BONUS = 5;
    /** Le nombre de robots achevés qui fait gagner un niveau au réseau. */
    public static final int KILLS_PER_LEVEL = 3;

    private int robotsKilled;

    /** Crée un data center qui n'a encore enregistré aucune défaite. */
    public DataCenter() {
    }

    @Override
    public void onRobotKilled(RobotKilledEvent event) {
        robotsKilled++;
    }

    /** {@return le nombre de robots achevés depuis le début de la partie} */
    public int getRobotsKilled() { return robotsKilled; }

    /** {@return les niveaux ajoutés aux robots rencontrés, entre 0 et {@value #MAX_BONUS}} */
    public int getNetworkBonus() {
        return Math.min(MAX_BONUS, robotsKilled / KILLS_PER_LEVEL);
    }
}
