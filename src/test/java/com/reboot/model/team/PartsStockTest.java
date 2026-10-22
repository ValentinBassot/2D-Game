package com.reboot.model.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.event.EventBus;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.event.RobotReprogrammedEvent;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class PartsStockTest {

    private final EventBus bus = new EventBus();
    private final PartsStock stock = new PartsStock();

    PartsStockTest() {
        bus.subscribe(stock);
    }

    private static TankBot brokenAlly() {
        TankBot robot = new TankBot(1);
        robot.reprogram();
        robot.takeDamage(999);
        robot.handleKnockOut();
        return robot;
    }

    @Test
    void higherLevelRobots_giveMoreParts() {
        assertEquals(1, PartsStock.partsFor(new TankBot(2)));
        assertEquals(2, PartsStock.partsFor(new TankBot(3)));
        assertEquals(3, PartsStock.partsFor(new TankBot(8)));
    }

    @Test
    void killingARobot_givesParts_reprogrammingDoesNot() {
        bus.publish(new RobotKilledEvent(new HealerBot(5)));
        bus.publish(new RobotReprogrammedEvent(new HealerBot(5)));
        assertEquals(2, stock.getParts());
    }

    @Test
    void aPart_speedsUpTheRepair() {
        bus.publish(new RobotKilledEvent(new HealerBot(8)));
        TankBot robot = brokenAlly();

        assertTrue(stock.repair(robot));
        assertTrue(stock.repair(robot));
        assertTrue(stock.repair(robot));

        assertFalse(robot.isUnderRepair());
        assertEquals(0, stock.getParts());
    }

    @Test
    void noPart_noRepair() {
        assertFalse(stock.repair(brokenAlly()));
    }

    @Test
    void aRobotThatIsNotUnderRepair_keepsTheParts() {
        bus.publish(new RobotKilledEvent(new HealerBot(1)));
        TankBot ally = new TankBot(1);
        ally.reprogram();
        assertFalse(stock.repair(ally));
        assertEquals(1, stock.getParts());
    }
}
