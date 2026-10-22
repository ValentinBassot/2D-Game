package com.reboot.model.fighter;

/** État d'un robot reprogrammé. Il se bat pour le joueur et part en réparation s'il tombe K.O. */
public class AllyState implements RobotState {

    /** Crée l'état d'un robot allié. */
    public AllyState() {
    }

    @Override
    public boolean canFightForPlayer() {
        return true;
    }

    @Override
    public boolean isReprogrammable() {
        return false;
    }

    @Override
    public RobotState onReprogram() {
        throw new IllegalStateException("An ally cannot be reprogrammed");
    }

    @Override
    public RobotState onKnockOut() {
        return new RepairingState();
    }

    @Override
    public RobotState onTeamVictory() {
        return this;
    }

    @Override
    public RobotState onPartUsed() {
        return this;
    }
}
