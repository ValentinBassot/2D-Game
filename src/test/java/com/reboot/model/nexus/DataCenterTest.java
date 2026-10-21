package com.reboot.model.nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.reboot.model.event.EventBus;
import com.reboot.model.event.RobotKilledEvent;
import com.reboot.model.event.RobotReprogrammedEvent;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class DataCenterTest {

    private final EventBus bus = new EventBus();
    private final DataCenter dataCenter = new DataCenter();

    DataCenterTest() {
        bus.subscribe(dataCenter);
    }

    private void kill(int count) {
        for (int i = 0; i < count; i++) {
            bus.publish(new RobotKilledEvent(new TankBot(1)));
        }
    }

    @Test
    void theNetwork_gainsALevelEveryThreeKills() {
        kill(2);
        assertEquals(0, dataCenter.getNetworkBonus());
        kill(1);
        assertEquals(1, dataCenter.getNetworkBonus());
        kill(3);
        assertEquals(2, dataCenter.getNetworkBonus());
        assertEquals(6, dataCenter.getRobotsKilled());
    }

    @Test
    void theBonus_isCappedAtFive() {
        kill(30);
        assertEquals(DataCenter.MAX_BONUS, dataCenter.getNetworkBonus());
    }

    @Test
    void reprogramming_doesNotFeedTheNetwork() {
        bus.publish(new RobotReprogrammedEvent(new TankBot(1)));
        assertEquals(0, dataCenter.getRobotsKilled());
    }
}
