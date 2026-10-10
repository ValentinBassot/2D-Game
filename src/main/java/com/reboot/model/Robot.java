package com.reboot.model;

public abstract class Robot extends Combattant {

    public static final Skill STRIKE = new Skill("Frappe", 10, false);

    private final RobotType type;

    protected Robot(String name, RobotType type, int level,
                    int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        super(name, level, baseHp, baseAtk, baseDef, baseSpeed);
        this.type = type;
        addSkill(STRIKE);
    }

    public RobotType getType() {
        return type;
    }
}