package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CombatTest {

    private static class Dummy extends Combattant {
        Dummy(String name, int hp, int atk, int def, int speed) {
            super(name, 1, hp, atk, def, speed);
            addSkill(Robot.STRIKE);
        }
    }

    @Test
    void fasterFighter_attacksFirst_thenSlowerRipostes() {
        InfighterBot player = new InfighterBot(1);
        TankBot enemy = new TankBot(1);
        Combat combat = new Combat(player, enemy);

        TurnResult result = combat.useSkill(InfighterBot.FLURRY);

        assertEquals(42, enemy.getHp());
        assertEquals(27, player.getHp());
        assertEquals(2, result.log().size());
        assertTrue(result.log().get(0).startsWith("InfighterBot"));
        assertFalse(result.combatOver());
        assertEquals(1, combat.getTurn());
    }

    @Test
    void fasterEnemy_attacksFirst() {
        TankBot player = new TankBot(1);
        InfighterBot enemy = new InfighterBot(1);
        Combat combat = new Combat(player, enemy);

        TurnResult result = combat.useSkill(TankBot.CRUSH);

        assertTrue(result.log().get(0).startsWith("InfighterBot utilise Frappe"));
        assertEquals(48, player.getHp());
        assertEquals(18, enemy.getHp());
    }

    @Test
    void knockOut_stopsTheRiposte() {
        InfighterBot player = new InfighterBot(1);
        HealerBot enemy = new HealerBot(1);
        Combat combat = new Combat(player, enemy);

        combat.useSkill(InfighterBot.FLURRY);
        assertEquals(34, player.getHp());

        TurnResult result = combat.useSkill(InfighterBot.FLURRY);

        assertTrue(enemy.isKo());
        assertEquals(34, player.getHp());
        assertEquals(2, result.log().size());
        assertTrue(result.combatOver());
        assertEquals(CombatOutcome.VICTORY, combat.getOutcome());
    }

    @Test
    void playerKnockedOut_isADefeat() {
        Combat combat = new Combat(new HealerBot(1), new InfighterBot(5));
        while (!combat.isOver()) {
            combat.useSkill(HealerBot.CORROSIVE_NANOBOTS);
        }
        assertEquals(CombatOutcome.DEFEAT, combat.getOutcome());
    }

    @Test
    void equalSpeed_playerAttacksFirst() {
        Dummy player = new Dummy("Joueur", 10, 100, 0, 5);
        Dummy enemy = new Dummy("Ennemi", 10, 100, 0, 5);
        Combat combat = new Combat(player, enemy);

        combat.useSkill(Robot.STRIKE);

        assertTrue(enemy.isKo());
        assertFalse(player.isKo());
        assertEquals(CombatOutcome.VICTORY, combat.getOutcome());
    }

    @Test
    void attackingAfterTheEnd_isRejected() {
        Combat combat = new Combat(new HealerBot(1), new InfighterBot(5));
        while (!combat.isOver()) {
            combat.useSkill(Robot.STRIKE);
        }
        assertThrows(IllegalStateException.class, () -> combat.useSkill(Robot.STRIKE));
    }

    @Test
    void outcomeBeforeTheEnd_isRejected() {
        Combat combat = new Combat(new TankBot(1), new TankBot(1));
        assertThrows(IllegalStateException.class, combat::getOutcome);
    }

    @Test
    void unknownSkill_isRejected() {
        Combat combat = new Combat(new InfighterBot(1), new TankBot(1));
        assertThrows(IllegalArgumentException.class, () -> combat.useSkill(TankBot.CRUSH));
    }
}