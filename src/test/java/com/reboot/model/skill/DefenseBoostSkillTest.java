package com.reboot.model.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class DefenseBoostSkillTest {

    @Test
    void apply_boostsTheUser_notTheTarget() {
        TankBot user = new TankBot(1);
        InfighterBot target = new InfighterBot(1);

        String log = TankBot.ARMOR.apply(user, target);

        assertEquals(24, user.getDef());
        assertEquals(6, target.getDef());
        assertEquals("TankBot utilise Blindage : DEF x2 pendant 2 tours.", log);
    }

    @Test
    void invalidBoost_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DefenseBoostSkill("Bug", 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new DefenseBoostSkill("Bug", 2, 0));
    }

    @Test
    void armor_keepsItsSettings() {
        assertEquals("Blindage", TankBot.ARMOR.getName());
        assertEquals(2, TankBot.ARMOR.getMultiplier());
        assertEquals(2, TankBot.ARMOR.getTurns());
    }
}
