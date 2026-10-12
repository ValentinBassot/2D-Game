package com.reboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RobotTypeTest {

    @Test
    void weaknessTriangle_isRespected() {
        assertTrue(RobotType.TANK.beats(RobotType.INFIGHTER));
        assertTrue(RobotType.INFIGHTER.beats(RobotType.HEALER));
        assertTrue(RobotType.HEALER.beats(RobotType.TANK));
    }

    @Test
    void advantage_onlyWorksOneWay() {
        assertFalse(RobotType.INFIGHTER.beats(RobotType.TANK));
        assertFalse(RobotType.HEALER.beats(RobotType.INFIGHTER));
        assertFalse(RobotType.TANK.beats(RobotType.HEALER));
    }

    @Test
    void aType_doesNotBeatItself() {
        for (RobotType type : RobotType.values()) {
            assertFalse(type.beats(type));
        }
    }

    @Test
    void withAdvantage_multiplierIsOnePointFive() {
        assertEquals(1.5, RobotType.TANK.multiplierAgainst(RobotType.INFIGHTER));
    }

    @Test
    void withoutAdvantage_multiplierIsOne_notZero() {
        assertEquals(1.0, RobotType.TANK.multiplierAgainst(RobotType.HEALER));
        assertEquals(1.0, RobotType.TANK.multiplierAgainst(RobotType.TANK));
    }
}