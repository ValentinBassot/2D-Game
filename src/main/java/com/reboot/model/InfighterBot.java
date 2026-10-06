package com.reboot.model;

public class InfighterBot extends Robot {

    public static final Skill RAFALE_DE_COUPS = new Skill("Rafale de coups", 16, false);
    public static final Skill CROCHET = new Skill("Crochet", 12, false);

    private static final int PV_BASE = 45;
    private static final int ATK_BASE = 14;
    private static final int DEF_BASE = 6;
    private static final int VIT_BASE = 10;

    public InfighterBot(int niveau) {
        super("InfighterBot", TypeRobot.INFIGHTER, niveau, PV_BASE, ATK_BASE, DEF_BASE, VIT_BASE);
        ajouterCompetence(RAFALE_DE_COUPS);
        ajouterCompetence(CROCHET);
    }
}