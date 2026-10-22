package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.HealSkill;

/** Le soigneur de l'équipe. Il se répare et attaque à travers la défense. */
public class HealerBot extends Robot {

    /** Patch réseau, rend 15 PV. */
    public static final HealSkill NETWORK_PATCH = new HealSkill("Patch réseau", 15);
    /** Nanobots corrosifs, puissance 12, ignorent la défense. */
    public static final DamageSkill CORROSIVE_NANOBOTS = new DamageSkill("Nanobots corrosifs", 12, true, false);

    private static final int BASE_HP = 45;
    private static final int BASE_ATK = 7;
    private static final int BASE_DEF = 8;
    private static final int BASE_SPEED = 9;

    /**
     * Crée un HealerBot détourné.
     *
     * @param level son niveau
     */
    public HealerBot(int level) {
        super("HealerBot", RobotType.HEALER, level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
        addSkill(NETWORK_PATCH);
        addSkill(CORROSIVE_NANOBOTS);
    }
}