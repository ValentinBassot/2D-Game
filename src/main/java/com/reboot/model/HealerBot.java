package com.reboot.model;

public class HealerBot extends Robot {

    public static final Skill NETWORK_PATCH = new Skill("Patch réseau", 0, false);
    public static final Skill CORROSIVE_NANOBOTS = new Skill("Nanobots corrosifs", 12, true);

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