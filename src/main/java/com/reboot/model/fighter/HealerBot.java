package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;

public class HealerBot extends Robot {

    public static final DamageSkill NETWORK_PATCH = new DamageSkill("Patch réseau", 0);
    public static final DamageSkill CORROSIVE_NANOBOTS = new DamageSkill("Nanobots corrosifs", 12, true, false);

    private static final int BASE_HP = 45;
    private static final int BASE_ATK = 7;
    private static final int BASE_DEF = 8;
    private static final int BASE_SPEED = 9;

    public HealerBot(int level) {
        super("HealerBot", RobotType.HEALER, level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
        addSkill(NETWORK_PATCH);
        addSkill(CORROSIVE_NANOBOTS);
    }
}