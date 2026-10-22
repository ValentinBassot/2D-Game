package com.reboot.model.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class DamageSkillTest {

    @Test
    void shortConstructor_hasNoPriorityAndKeepsDefense() {
        DamageSkill flurry = new DamageSkill("Rafale de coups", 16);
        assertEquals("Rafale de coups", flurry.getName());
        assertEquals(16, flurry.getPower());
        assertFalse(flurry.ignoresDefense());
        assertFalse(flurry.hasPriority());
    }

    @Test
    void fullConstructor_keepsItsFlags() {
        DamageSkill skill = new DamageSkill("Test", 12, true, true);
        assertTrue(skill.ignoresDefense());
        assertTrue(skill.hasPriority());
    }

    @Test
    void apply_removesHpFromTheTarget() {
        InfighterBot user = new InfighterBot(1);
        TankBot target = new TankBot(1);

        String log = InfighterBot.FLURRY.apply(user, target);

        assertEquals(42, target.getHp());
        assertEquals(45, user.getHp());
        assertEquals("InfighterBot utilise Rafale de coups : 18 dégâts.", log);
    }

    @Test
    void negativePower_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DamageSkill("Bug", -1));
    }
}
