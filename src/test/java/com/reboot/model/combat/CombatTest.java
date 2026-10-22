package com.reboot.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import com.reboot.model.team.Team;
import java.util.List;
import org.junit.jupiter.api.Test;

class CombatTest {

    private static class Dummy extends Combattant {
        Dummy(String name, int hp, int atk, int def, int speed) {
            super(name, 1, hp, atk, def, speed);
            addSkill(Robot.STRIKE);
        }

        @Override
        public boolean isReprogrammable() {
            return false;
        }

        @Override
        public String getSpriteId() {
            return "dummy";
        }
    }

    private static class SlowHooker extends Combattant {
        SlowHooker() {
            super("Lent", 1, 100, 10, 0, 1);
            addSkill(InfighterBot.HOOK);
        }

        @Override
        public boolean isReprogrammable() {
            return false;
        }

        @Override
        public String getSpriteId() {
            return "dummy";
        }
    }

    @Test
    void prioritySkill_actsFirst_evenWhenSlower() {
        SlowHooker player = new SlowHooker();
        Dummy enemy = new Dummy("Rapide", 100, 10, 0, 50);
        Combat combat = new Combat(player, enemy);

        TurnResult result = combat.useSkill(InfighterBot.HOOK);

        assertTrue(result.log().get(0).startsWith("Lent utilise Crochet"));
        assertEquals(78, enemy.getHp());
    }

    @Test
    void networkPatch_healsTheHealer_andDoesNotHurtTheEnemy() {
        HealerBot player = new HealerBot(1);
        TankBot enemy = new TankBot(1);
        Combat combat = new Combat(player, enemy);

        combat.useSkill(HealerBot.NETWORK_PATCH);
        assertEquals(35, player.getHp());

        TurnResult result = combat.useSkill(HealerBot.NETWORK_PATCH);

        assertEquals("HealerBot utilise Patch réseau : + 10 PV.", result.log().get(0));
        assertEquals(60, enemy.getHp());
    }

    @Test
    void armor_reducesDamageForTwoTurns_thenExpires() {
        TankBot player = new TankBot(1);
        InfighterBot enemy = new InfighterBot(1);
        Combat combat = new Combat(player, enemy);

        combat.useSkill(TankBot.ARMOR);
        assertEquals(48, player.getHp());

        combat.useSkill(TankBot.CRUSH);
        assertEquals(47, player.getHp());

        combat.useSkill(TankBot.CRUSH);
        assertEquals(35, player.getHp());
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
        assertEquals(List.of("InfighterBot utilise Rafale de coups : 33 dégâts.", "HealerBot est K.O. !",
                "InfighterBot gagne 10 XP."), result.log());
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

    private static <T extends Robot> T ally(T robot) {
        robot.reprogram();
        return robot;
    }

    @Test
    void switching_costsTheTurn_andTheEnemyHitsTheNewcomer() {
        InfighterBot starter = ally(new InfighterBot(1));
        TankBot tank = ally(new TankBot(1));
        Team team = new Team(starter);
        team.add(tank);
        Combat combat = new Combat(team, new HealerBot(1));

        TurnResult result = combat.switchTo(tank);

        assertSame(tank, combat.getPlayer());
        assertEquals("InfighterBot revient. TankBot entre en combat !", result.log().get(0));
        assertEquals(53, tank.getHp());
        assertEquals(45, starter.getHp());
        assertEquals(1, combat.getTurn());
    }

    @Test
    void aKnockedOutRobot_isReplacedForFree_andTheCombatGoesOn() {
        InfighterBot starter = ally(new InfighterBot(1));
        TankBot tank = ally(new TankBot(1));
        starter.takeDamage(44);
        Team team = new Team(starter);
        team.add(tank);
        InfighterBot enemy = new InfighterBot(5);
        Combat combat = new Combat(team, enemy);

        TurnResult knockOut = combat.useSkill(InfighterBot.FLURRY);

        assertFalse(knockOut.combatOver());
        assertTrue(combat.mustSwitch());
        assertThrows(IllegalStateException.class, () -> combat.useSkill(InfighterBot.FLURRY));

        TurnResult entry = combat.switchTo(tank);

        assertEquals(List.of("TankBot entre en combat !"), entry.log());
        assertEquals(60, tank.getHp());
        assertEquals(enemy.getMaxHp(), enemy.getHp());
        assertEquals(1, combat.getTurn());
        assertFalse(combat.mustSwitch());
    }

    @Test
    void whenNoRobotIsLeft_theCombatIsLost() {
        InfighterBot starter = ally(new InfighterBot(1));
        starter.takeDamage(44);
        Combat combat = new Combat(new Team(starter), new InfighterBot(5));

        assertTrue(combat.useSkill(InfighterBot.FLURRY).combatOver());
        assertEquals(CombatOutcome.DEFEAT, combat.getOutcome());
    }

    @Test
    void switchOptions_skipTheActiveRobot_andTheRobotsUnderRepair() {
        InfighterBot starter = ally(new InfighterBot(1));
        TankBot broken = ally(new TankBot(1));
        broken.takeDamage(999);
        broken.handleKnockOut();
        HealerBot healer = ally(new HealerBot(1));
        Team team = new Team(starter);
        team.add(broken);
        team.add(healer);
        Combat combat = new Combat(team, new TankBot(1));

        assertEquals(List.of(healer), combat.getSwitchOptions());
        assertThrows(IllegalArgumentException.class, () -> combat.switchTo(broken));
        assertThrows(IllegalArgumentException.class, () -> combat.switchTo(starter));
    }

    @Test
    void aSoloCombat_offersNoSwitch() {
        assertTrue(new Combat(new InfighterBot(1), new TankBot(1)).getSwitchOptions().isEmpty());
    }

    @Test
    void theRobotThatWins_getsTheXp_andCanLevelUp() {
        InfighterBot player = new InfighterBot(1);
        Combat combat = new Combat(player, new HealerBot(5));

        while (!combat.isOver()) {
            combat.useSkill(InfighterBot.FLURRY);
        }

        assertEquals(CombatOutcome.VICTORY, combat.getOutcome());
        assertEquals(2, player.getLevel());
        assertEquals(0, player.getXp());
    }

    @Test
    void levelingUp_isWrittenInTheLog() {
        InfighterBot player = new InfighterBot(1);
        player.gainXp(45);
        HealerBot enemy = new HealerBot(1);
        enemy.takeDamage(44);

        TurnResult result = new Combat(player, enemy).useSkill(InfighterBot.FLURRY);

        assertTrue(result.log().contains("InfighterBot passe au niveau 2 !"));
    }

    @Test
    void losing_givesNoXpToTheEnemy() {
        InfighterBot enemy = new InfighterBot(5);
        Combat combat = new Combat(new HealerBot(1), enemy);
        while (!combat.isOver()) {
            combat.useSkill(Robot.STRIKE);
        }
        assertEquals(0, enemy.getXp());
    }

    private static class Unarmed extends Combattant {
        Unarmed() {
            super("Sans arme", 1, 30, 10, 0, 1);
        }

        @Override
        public boolean isReprogrammable() {
            return false;
        }

        @Override
        public String getSpriteId() {
            return "unarmed";
        }
    }

    @Test
    void anEnemyWithoutSkill_fallsBackOnStrike() {
        TurnResult result = new Combat(new TankBot(1), new Unarmed()).useSkill(TankBot.CRUSH);
        assertTrue(result.log().get(1).startsWith("Sans arme utilise Frappe"));
    }

    @Test
    void aSoloCombat_hasAnEmptyRoster() {
        assertTrue(new Combat(new TankBot(1), new HealerBot(1)).getRoster().isEmpty());
    }
}
