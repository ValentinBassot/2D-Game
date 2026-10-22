package com.reboot.model.nexus;

import com.reboot.model.event.GameEventListener;
import com.reboot.model.event.PilotNeutralizedEvent;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.event.RobotReprogrammedEvent;

/**
 * La barre de recherche : à quel point NEXUS traque le joueur, de 0 à 100.
 *
 * <p>Elle n'est modifiée que par les événements du jeu, jamais directement :
 * achever un robot la fait monter, le reprogrammer la fait baisser.
 */
public class WantedBar implements GameEventListener {

    /** La valeur maximale de la barre. */
    public static final int MAX = 100;
    /** Ce qu'ajoute un robot achevé. */
    public static final int KILL = 15;
    /** Ce qu'ajoute un pilote neutralisé. */
    public static final int PILOT = 10;
    /** Ce que retire un robot reprogrammé. */
    public static final int REPROGRAM = 5;

    private int value;

    @Override
    public void onRobotKilled(RobotKilledEvent event) {
        change(KILL);
    }

    @Override
    public void onRobotReprogrammed(RobotReprogrammedEvent event) {
        change(-REPROGRAM);
    }

    @Override
    public void onPilotNeutralized(PilotNeutralizedEvent event) {
        change(PILOT);
    }

    private void change(int delta) {
        value = Math.max(0, Math.min(MAX, value + delta));
    }

    /** @return la valeur actuelle, entre 0 et 100 */
    public int getValue() { return value; }

    /** @return le palier atteint par la valeur actuelle */
    public WantedTier getTier() { return WantedTier.of(value); }
}
