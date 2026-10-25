package com.reboot.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.TankBot;
import com.reboot.model.skill.Skill;
import com.reboot.model.team.Team;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CombatStrategyTest {

    private static final TankBot ROBOT = new TankBot(1);
    private static final List<Skill> SKILLS = ROBOT.getSkills();

    @Test
    void firstSkillStrategy_picksTheFirstUsableSkill() {
        assertSame(SKILLS.get(0), new FirstSkillStrategy().chooseSkill(ROBOT, SKILLS, ROBOT));
    }

    @Test
    void randomStrategy_onlyPicksUsableSkills() {
        RandomStrategy strategy = new RandomStrategy(new Random(42));
        for (int i = 0; i < 50; i++) {
            assertTrue(SKILLS.contains(strategy.chooseSkill(ROBOT, SKILLS, ROBOT)));
        }
    }

    @Test
    void randomStrategy_usesEverySkillOverTime() {
        RandomStrategy strategy = new RandomStrategy(new Random(42));
        Set<Skill> used = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            used.add(strategy.chooseSkill(ROBOT, SKILLS, ROBOT));
        }
        assertEquals(SKILLS.size(), used.size());
    }

    @Test
    void randomStrategy_isReproducibleWithTheSameSeed() {
        RandomStrategy first = new RandomStrategy(new Random(7));
        RandomStrategy second = new RandomStrategy(new Random(7));
        for (int i = 0; i < 20; i++) {
            assertSame(first.chooseSkill(ROBOT, SKILLS, ROBOT), second.chooseSkill(ROBOT, SKILLS, ROBOT));
        }
    }

    @Test
    void randomStrategy_rejectsAMissingRandom() {
        assertThrows(NullPointerException.class, () -> new RandomStrategy(null));
    }

    @Test
    void combat_asksItsStrategy() {
        TankBot starter = new TankBot(1);
        starter.reprogram();
        Combat combat = new Combat(new Team(starter), new InfighterBot(1),
                (attacker, usable, target) -> InfighterBot.HOOK);
        TurnResult result = combat.useSkill(Combattant.STRIKE);
        assertTrue(String.join(" ", result.log()).contains(InfighterBot.HOOK.getName()));
    }

    @Test
    void combat_rejectsAMissingStrategy() {
        TankBot starter = new TankBot(1);
        starter.reprogram();
        assertThrows(NullPointerException.class, () -> new Combat(new Team(starter), new TankBot(1), null));
    }
}
