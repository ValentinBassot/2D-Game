package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;

public class InfighterBot extends Robot {

    public static final DamageSkill FLURRY = new DamageSkill("Rafale de coups", 16);
    public static final DamageSkill HOOK = new DamageSkill("Crochet", 12);

    private static final int BASE_HP = 45;
    private static final int BASE_ATK = 14;
    private static final int BASE_DEF = 6;
    private static final int BASE_SPEED = 10;

    public InfighterBot(int level) {
        super("InfighterBot", RobotType.INFIGHTER, level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
        addSkill(FLURRY);
        addSkill(HOOK);
    }
}