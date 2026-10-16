package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;

public class TankBot extends Robot {

    public static final DamageSkill ARMOR = new DamageSkill("Blindage", 0);
    public static final DamageSkill CRUSH = new DamageSkill("Écrasement", 16);

    private static final int BASE_HP = 60;
    private static final int BASE_ATK = 8;
    private static final int BASE_DEF = 12;
    private static final int BASE_SPEED = 4;

    public TankBot(int level) {
        super("TankBot", RobotType.TANK, level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
        addSkill(ARMOR);
        addSkill(CRUSH);
    }
}