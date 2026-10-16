package com.reboot.model.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.TankBot;
import org.junit.jupiter.api.Test;

class HealSkillTest {

    @Test
    void apply_healsTheUser_notTheTarget() {
        HealerBot user = new HealerBot(1);
        TankBot target = new TankBot(1);
        user.takeDamage(20);

        String log = HealerBot.NETWORK_PATCH.apply(user, target);

        assertEquals(40, user.getHp());
        assertEquals(60, target.getHp());
        assertEquals("HealerBot utilise Patch réseau : + 15 PV.", log);
    }

    @Test
    void apply_neverExceedsMaxHp_andLogsTheRealAmount() {
        HealerBot user = new HealerBot(1);
        user.takeDamage(5);

        String log = HealerBot.NETWORK_PATCH.apply(user, new TankBot(1));

        assertEquals(45, user.getHp());
        assertEquals("HealerBot utilise Patch réseau : + 5 PV.", log);
    }

    @Test
    void nonPositiveAmount_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HealSkill("Bug", 0));
    }
}
