package com.reboot.model.fighter;

public interface RobotState {

    boolean canFightForPlayer();

    boolean isReprogrammable();

    default boolean isUnderRepair() {
        return false;
    }

    RobotState onReprogram();

    RobotState onKnockOut();

    RobotState onTeamVictory();

    RobotState onPartUsed();
}
