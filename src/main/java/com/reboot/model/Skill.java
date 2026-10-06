package com.reboot.model;

public class Skill {
    private final String nom;
    private final int puissance;
    private final boolean ignoreDef;

    public Skill(String nom, int puissance, boolean ignoreDef) {
        if (puissance < 0) {
            throw new IllegalArgumentException("La puissance ne peut pas être négative");
        }
        this.nom = nom;
        this.puissance = puissance;
        this.ignoreDef = ignoreDef;
    }

    public String getNom() { return nom; }
    public int getPuissance() { return puissance; }
    public boolean ignoreDef() { return ignoreDef; }
}
