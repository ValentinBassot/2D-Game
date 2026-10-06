package com.reboot.model;

public final class CalculDegats {

    private CalculDegats() {
    }

    public static int calculer(Combattant attaquant, Skill competence, Combattant defenseur) {
        int base = competence.getPuissance() + attaquant.getAtk();
        if (!competence.ignoreDef()) {
            base -= defenseur.getDef();
        }
        double degats = base * multiplicateur(attaquant, defenseur);
        return Math.max(1, (int) Math.floor(degats));
    }

    private static double multiplicateur(Combattant attaquant, Combattant defenseur) {
        if (attaquant instanceof Robot a && defenseur instanceof Robot d) {
            return a.getType().multiplicateurContre(d.getType());
        }
        return 1.0;
    }
}