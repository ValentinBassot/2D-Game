package com.reboot.model;

public class InfighterBot extends Robot {

    public static final Skill FLURRY = new Skill("Rafale de coups", 16, false);
    public static final Skill HOOK = new Skill("Crochet", 12, false);

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