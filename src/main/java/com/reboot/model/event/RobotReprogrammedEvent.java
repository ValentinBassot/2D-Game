package com.reboot.model.event;

import com.reboot.model.fighter.Robot;

/**
 * Le joueur a reprogrammé un robot vaincu, qui devient son allié.
 *
 * @param robot le robot reprogrammé
 */
public record RobotReprogrammedEvent(Robot robot) implements GameEvent {

    @Override
    public void dispatchTo(GameEventListener listener) {
        listener.onRobotReprogrammed(this);
    }
}
