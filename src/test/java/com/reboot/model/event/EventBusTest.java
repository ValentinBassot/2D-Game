package com.reboot.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.reboot.model.fighter.Exosquelette;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.TankBot;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventBusTest {

    private static class Recorder implements GameEventListener {
        private final List<String> received = new ArrayList<>();
        private final String name;

        Recorder(String name) {
            this.name = name;
        }

        @Override
        public void onRobotKilled(RobotKilledEvent event) {
            received.add(name + ":killed " + event.robot().getName());
        }

        @Override
        public void onRobotReprogrammed(RobotReprogrammedEvent event) {
            received.add(name + ":reprogrammed " + event.robot().getName());
        }

        @Override
        public void onPilotNeutralized(PilotNeutralizedEvent event) {
            received.add(name + ":pilot " + event.exosquelette().getName());
        }
    }

    private static class OnlyKills implements GameEventListener {
        private int kills;

        @Override
        public void onRobotKilled(RobotKilledEvent event) {
            kills++;
        }
    }

    @Test
    void eachEvent_reachesTheMatchingMethod() {
        EventBus bus = new EventBus();
        Recorder recorder = new Recorder("r");
        bus.subscribe(recorder);

        bus.publish(new RobotKilledEvent(new TankBot(1)));
        bus.publish(new RobotReprogrammedEvent(new HealerBot(1)));
        bus.publish(new PilotNeutralizedEvent(new Exosquelette(4)));

        assertEquals(List.of("r:killed TankBot", "r:reprogrammed HealerBot", "r:pilot Exosquelette"),
                recorder.received);
    }

    @Test
    void everySubscriber_isNotified_inSubscriptionOrder() {
        EventBus bus = new EventBus();
        List<String> shared = new ArrayList<>();
        Recorder first = new Recorder("a");
        Recorder second = new Recorder("b");
        bus.subscribe(first);
        bus.subscribe(second);

        bus.publish(new RobotKilledEvent(new TankBot(1)));

        shared.addAll(first.received);
        shared.addAll(second.received);
        assertEquals(List.of("a:killed TankBot", "b:killed TankBot"), shared);
    }

    @Test
    void aListener_ignoresTheEventsItDoesNotOverride() {
        EventBus bus = new EventBus();
        OnlyKills listener = new OnlyKills();
        bus.subscribe(listener);

        bus.publish(new RobotReprogrammedEvent(new TankBot(1)));
        bus.publish(new RobotKilledEvent(new TankBot(1)));

        assertEquals(1, listener.kills);
    }

    @Test
    void nullListenerOrEvent_isRejected() {
        EventBus bus = new EventBus();
        assertThrows(NullPointerException.class, () -> bus.subscribe(null));
        assertThrows(NullPointerException.class, () -> bus.publish(null));
    }

    @Test
    void aListenerThatOverridesNothing_ignoresEveryEvent() {
        EventBus bus = new EventBus();
        bus.subscribe(new GameEventListener() { });
        bus.publish(new RobotKilledEvent(new TankBot(1)));
        bus.publish(new RobotReprogrammedEvent(new TankBot(1)));
        bus.publish(new PilotNeutralizedEvent(new Exosquelette(4)));
    }
}
