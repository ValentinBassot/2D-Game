package com.reboot.model;

public class TankBot extends Robot {

    public static final Skill BLINDAGE = new Skill("Blindage", 0, false);
    public static final Skill ECRASEMENT = new Skill("Écrasement", 16, false);

    private static final int PV_BASE = 60;
    private static final int ATK_BASE = 8;
    private static final int DEF_BASE = 12;
    private static final int VIT_BASE = 4;

    public TankBot(int niveau) {
        super("TankBot", TypeRobot.TANK, niveau, PV_BASE, ATK_BASE, DEF_BASE, VIT_BASE);
        ajouterCompetence(BLINDAGE);
        ajouterCompetence(ECRASEMENT);
    }
}