package com.reboot.model.fighter;

public interface RobotState {

    boolean canFightForPlayer();

    boolean isReprogrammable();

    RobotState onReprogram();

    RobotState onKnockOut();

    RobotState onTeamVictory();

    RobotState onPartUsed();
}
