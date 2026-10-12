package com.reboot.model;

public class TankBot extends Robot {

    public static final Skill ARMOR = new Skill("Blindage", 0, false);
    public static final Skill CRUSH = new Skill("Écrasement", 16, false);

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