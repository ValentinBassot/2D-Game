package com.reboot.model.nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.reboot.model.event.EventBus;
import com.reboot.model.event.PilotNeutralizedEvent;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.event.RobotReprogrammedEvent;
import com.reboot.model.fighter.Exosquelette;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class WantedBarTest {

    private final EventBus bus = new EventBus();
    private final WantedBar bar = new WantedBar();

    WantedBarTest() {
        bus.subscribe(bar);
    }

    @Test
    void theBar_startsEmpty() {
        assertEquals(0, bar.getValue());
        assertEquals(WantedTier.UNKNOWN, bar.getTier());
    }

    @Test
    void eachChoice_movesTheBar() {
        bus.publish(new RobotKilledEvent(new TankBot(1)));
        assertEquals(15, bar.getValue());
        bus.publish(new PilotNeutralizedEvent(new Exosquelette(4)));
        assertEquals(25, bar.getValue());
        bus.publish(new RobotReprogrammedEvent(new TankBot(1)));
        assertEquals(20, bar.getValue());
    }

    @Test
    void theBar_neverGoesBelowZero() {
        bus.publish(new RobotReprogrammedEvent(new TankBot(1)));
        assertEquals(0, bar.getValue());
    }

    @Test
    void theBar_neverGoesAboveOneHundred() {
        for (int i = 0; i < 10; i++) {
            bus.publish(new RobotKilledEvent(new TankBot(1)));
        }
        assertEquals(WantedBar.MAX, bar.getValue());
        assertEquals(WantedTier.PUBLIC_ENEMY, bar.getTier());
    }

    @Test
    void tiers_followTheGddThresholds() {
        assertEquals(WantedTier.UNKNOWN, WantedTier.of(24));
        assertEquals(WantedTier.SPOTTED, WantedTier.of(25));
        assertEquals(WantedTier.SPOTTED, WantedTier.of(49));
        assertEquals(WantedTier.HUNTED, WantedTier.of(50));
        assertEquals(WantedTier.HUNTED, WantedTier.of(74));
        assertEquals(WantedTier.PUBLIC_ENEMY, WantedTier.of(75));
        assertEquals("Ennemi public", WantedTier.PUBLIC_ENEMY.getLabel());
    }

    @Test
    void tiers_exposeTheirThreshold() {
        assertEquals(75, WantedTier.PUBLIC_ENEMY.getThreshold());
    }
}
