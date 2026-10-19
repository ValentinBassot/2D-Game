package com.reboot.model;

public class AllyState implements RobotState {

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
