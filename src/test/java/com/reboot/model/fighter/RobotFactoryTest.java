package com.reboot.model.fighter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RobotFactoryTest {

    private final RobotFactory factory = new RobotFactory(new Random(7));

    @Test
    void create_buildsTheRightRobot_hijacked_atTheRightLevel() {
        Robot tank = factory.create(RobotType.TANK, 4);
        assertInstanceOf(TankBot.class, tank);
        assertEquals(4, tank.getLevel());
        assertTrue(tank.isReprogrammable());
        assertInstanceOf(HealerBot.class, factory.create(RobotType.HEALER, 1));
        assertInstanceOf(InfighterBot.class, factory.create(RobotType.INFIGHTER, 1));
    }

    @Test
    void encounters_onlyUseTheZoneTypes() {
        Set<RobotType> seen = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            seen.add(factory.createEncounter(List.of(RobotType.TANK, RobotType.INFIGHTER), 5).getType());
        }
        assertEquals(Set.of(RobotType.TANK, RobotType.INFIGHTER), seen);
    }

    @Test
    void theNetworkBonus_raisesTheEncounterLevel() {
        assertEquals(2, RobotFactory.encounterLevel(2, 0));
        assertEquals(5, RobotFactory.encounterLevel(2, 3));
        assertEquals(1, RobotFactory.encounterLevel(0, 0));
    }

    @Test
    void aZoneWithoutRobotType_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> factory.createEncounter(List.of(), 2));
    }
}
