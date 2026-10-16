package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.DefenseBoostSkill;

public class TankBot extends Robot {

    public static final DefenseBoostSkill ARMOR = new DefenseBoostSkill("Blindage", 2, 2);
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