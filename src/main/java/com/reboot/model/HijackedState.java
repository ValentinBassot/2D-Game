package com.reboot.model;

public class HijackedState implements RobotState {

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
