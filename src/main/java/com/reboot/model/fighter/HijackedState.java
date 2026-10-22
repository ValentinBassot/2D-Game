package com.reboot.model.fighter;

/** État d'un robot piraté par NEXUS. Il se bat contre le joueur, qui peut le reprogrammer. */
public class HijackedState implements RobotState {

    /** Crée l'état d'un robot détourné par NEXUS. */
    public HijackedState() {
    }

    @Override
    public boolean canFightForPlayer() {
        return false;
    }

    @Override
    public boolean isReprogrammable() {
        return true;
    }

    @Override
    public RobotState onReprogram() {
        return new AllyState();
    }

    @Override
    public RobotState onKnockOut() {
        return this;
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
