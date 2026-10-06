package com.reboot.model;

public class HealerBot extends Robot {

    public static final Skill PATCH_RESEAU = new Skill("Patch réseau", 0, false);
    public static final Skill NANOBOTS_CORROSIFS = new Skill("Nanobots corrosifs", 12, true);

    private static final int PV_BASE = 45;
    private static final int ATK_BASE = 7;
    private static final int DEF_BASE = 8;
    private static final int VIT_BASE = 9;

    public HealerBot(int niveau) {
        super("HealerBot", TypeRobot.HEALER, niveau, PV_BASE, ATK_BASE, DEF_BASE, VIT_BASE);
        ajouterCompetence(PATCH_RESEAU);
        ajouterCompetence(NANOBOTS_CORROSIFS);
    }
}