package com.reboot.model.event;

import com.reboot.model.fighter.Robot;

/**
 * Le joueur a achevé un robot vaincu.
 *
 * @param robot le robot détruit
 */
public record RobotKilledEvent(Robot robot) implements GameEvent {

    @Override
    public void dispatchTo(GameEventListener listener) {
        listener.onRobotKilled(this);
    }
}
