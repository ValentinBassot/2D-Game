package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.DefenseBoostSkill;

/** Le tank, celui qui encaisse. */
public class TankBot extends Robot {

    /** Blindage, double la défense pendant 2 tours. */
    public static final DefenseBoostSkill ARMOR = new DefenseBoostSkill("Blindage", 2, 2);
    /** Écrasement, puissance 16. */
    public static final DamageSkill CRUSH = new DamageSkill("Écrasement", 16);

    private static final int BASE_HP = 60;
    private static final int BASE_ATK = 8;
    private static final int BASE_DEF = 12;
    private static final int BASE_SPEED = 4;

    /**
     * Crée un TankBot détourné.
     *
     * @param level son niveau
     */
    public TankBot(int level) {
        super("TankBot", RobotType.TANK, level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
        addSkill(ARMOR);
        addSkill(CRUSH);
    }
}