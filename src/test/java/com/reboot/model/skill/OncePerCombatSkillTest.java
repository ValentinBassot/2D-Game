package com.reboot.model.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class OncePerCombatSkillTest {

    @Test
    void keepsTheNameAndThePriorityOfItsSkill() {
        OncePerCombatSkill hook = new OncePerCombatSkill(InfighterBot.HOOK);
        assertEquals("Crochet", hook.getName());
        assertTrue(hook.hasPriority());
        assertTrue(hook.isOncePerCombat());
    }

    @Test
    void appliesTheEffectOfItsSkill() {
        TankBot target = new TankBot(1);
        new OncePerCombatSkill(new DamageSkill("Test", 10)).apply(new InfighterBot(1), target);
        assertTrue(target.getHp() < target.getMaxHp());
    }

    @Test
    void aNormalSkill_canBeUsedAgain() {
        assertFalse(InfighterBot.HOOK.isOncePerCombat());
    }

    @Test
    void rejectsAMissingSkill() {
        assertThrows(NullPointerException.class, () -> new OncePerCombatSkill(null));
    }
}
