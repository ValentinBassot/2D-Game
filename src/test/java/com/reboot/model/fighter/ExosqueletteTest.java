package com.reboot.model.fighter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExosqueletteTest {

    @Test
    void levelOne_hasBaseStats() {
        Exosquelette exo = new Exosquelette(1);
        assertEquals(55, exo.getMaxHp());
        assertEquals(11, exo.getAtk());
        assertEquals(10, exo.getDef());
        assertEquals(7, exo.getSpeed());
    }

    @Test
    void zoneOneChief_atLevelFour_isStronger() {
        Exosquelette exo = new Exosquelette(4);
        assertEquals(71, exo.getMaxHp());
        assertEquals(14, exo.getAtk());
    }

    @Test
    void knowsStrikeAndOverload() {
        assertEquals(List.of(Combattant.STRIKE, Exosquelette.OVERLOAD), new Exosquelette(1).getSkills());
    }

    @Test
    void aPilotedExosquelette_cannotBeReprogrammed() {
        assertFalse(new Exosquelette(1).isReprogrammable());
    }

    @Test
    void hasNoTypeAdvantage_inEitherDirection() {
        Exosquelette exo = new Exosquelette(1);
        assertEquals(1.0, exo.advantageAgainst(new HealerBot(1)));
        assertEquals(1.0, new InfighterBot(1).advantageAgainst(exo));
    }
}
