package com.reboot.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Exosquelette;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class EncounterTest {

    private static class Dummy extends Combattant {
        Dummy() {
            super("Dummy", 1, 10, 1, 1, 1);
            addSkill(Robot.STRIKE);
        }

        @Override
        public boolean isReprogrammable() {
            return false;
        }
    }

    private static void playUntilTheEnd(Encounter encounter) {
        while (!encounter.isFinished()) {
            encounter.getCombat().useSkill(Robot.STRIKE);
        }
    }

    @Test
    void victoryAgainstARobot_offersTheChoice() {
        HealerBot enemy = new HealerBot(1);
        Encounter encounter = new Encounter(new InfighterBot(10), enemy);
        playUntilTheEnd(encounter);
        EncounterResult result = encounter.getResult();
        assertEquals(CombatOutcome.VICTORY, result.outcome());
        assertSame(enemy, result.enemy());
        assertTrue(result.choiceAvailable());
    }

    @Test
    void victoryAgainstANonRobot_offersNoChoice() {
        Encounter encounter = new Encounter(new InfighterBot(10), new Dummy());
        playUntilTheEnd(encounter);
        EncounterResult result = encounter.getResult();
        assertEquals(CombatOutcome.VICTORY, result.outcome());
        assertFalse(result.choiceAvailable());
    }

    @Test
    void victoryAgainstAnExosquelette_offersNoChoice() {
        Encounter encounter = new Encounter(new InfighterBot(10), new Exosquelette(1));
        playUntilTheEnd(encounter);
        assertFalse(encounter.getResult().choiceAvailable());
    }

    @Test
    void defeat_offersNoChoice() {
        Encounter encounter = new Encounter(new HealerBot(1), new InfighterBot(10));
        playUntilTheEnd(encounter);
        EncounterResult result = encounter.getResult();
        assertEquals(CombatOutcome.DEFEAT, result.outcome());
        assertFalse(result.choiceAvailable());
    }

    @Test
    void resultBeforeTheEnd_isRejected() {
        Encounter encounter = new Encounter(new TankBot(1), new HealerBot(1));
        assertFalse(encounter.isFinished());
        assertThrows(IllegalStateException.class, encounter::getResult);
    }
}
