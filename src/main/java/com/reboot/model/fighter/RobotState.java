package com.reboot.model.fighter;

public interface RobotState {

    boolean canFightForPlayer();

    boolean isReprogrammable();

    default boolean isUnderRepair() {
        return false;
    }

    default int getVictoriesLeft() {
        return 0;
    }

    RobotState onReprogram();

    RobotState onKnockOut();

    RobotState onTeamVictory();

    RobotState onPartUsed();
}
