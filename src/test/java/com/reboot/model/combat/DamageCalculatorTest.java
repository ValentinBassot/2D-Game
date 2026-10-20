package com.reboot.model.combat;

import static org.junit.jupiter.api.Assertions.*;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import com.reboot.model.skill.Skill;
import org.junit.jupiter.api.Test;

class DamageCalculatorTest {

    private static class NonRobotFighter extends Combattant {
        NonRobotFighter() {
            super("Exo", 1, 60, 8, 12, 4);
        }
    }

    private static int hit(Combattant attacker, Skill skill, Combattant defender) {
        return DamageCalculator.compute(attacker, defender, skill.getPower(), skill.ignoresDefense());
    }

    @Test
    void withoutAdvantage_normalDamage() {
        assertEquals(16, hit(new TankBot(1), TankBot.CRUSH, new HealerBot(1)));
    }

    @Test
    void withAdvantage_onePointFiveBonus() {
        assertEquals(33, hit(new InfighterBot(1), InfighterBot.FLURRY, new HealerBot(1)));
    }

    @Test
    void tankAgainstInfighter_withStrike() {
        assertEquals(18, hit(new TankBot(1), Robot.STRIKE, new InfighterBot(1)));
    }

    @Test
    void nanobots_ignoreDefense_andRoundDown() {
        assertEquals(28, hit(new HealerBot(1), HealerBot.CORROSIVE_NANOBOTS, new TankBot(1)));
    }

    @Test
    void armorTooStrong_stillDealsOneDamage() {
        assertEquals(1, hit(new HealerBot(1), Robot.STRIKE, new TankBot(20)));
    }

    @Test
    void againstNonRobot_noTypeBonus() {
        assertEquals(18, hit(new InfighterBot(1), InfighterBot.FLURRY, new NonRobotFighter()));
    }

    @Test
    void calculator_doesNotRemoveHp() {
        HealerBot healer = new HealerBot(1);
        hit(new InfighterBot(1), InfighterBot.FLURRY, healer);
        assertEquals(45, healer.getHp());
    }
}