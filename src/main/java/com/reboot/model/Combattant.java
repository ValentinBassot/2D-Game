package com.reboot.model;

public abstract class Combattant {

    private final String nom;
    private final int niveau;
    private final int pvMax;
    private int pv;
    private final int atk;
    private final int def;
    private final int vit;

    protected Combattant(String nom, int niveau, int pvBase, int atkBase, int defBase, int vitBase) {
        if (niveau < 1) {
            throw new IllegalArgumentException("Le niveau doit être au moins 1");
        }
        this.nom = nom;
        this.niveau = niveau;
        this.pvMax = statAuNiveau(pvBase, niveau);
        this.pv = this.pvMax;
        this.atk = statAuNiveau(atkBase, niveau);
        this.def = statAuNiveau(defBase, niveau);
        this.vit = statAuNiveau(vitBase, niveau);
    }

    private static int statAuNiveau(int base, int niveau) {
        return base + base * (niveau - 1) / 10;
    }

    public void recevoirDegats(int degats) {
        if (degats < 0) {
            throw new IllegalArgumentException("Les dégâts ne peuvent pas être négatifs");
        }
        this.pv = Math.max(0, this.pv - degats);
    }

    public void soigner(int soin) {
        if (soin < 0) {
            throw new IllegalArgumentException("Le soin ne peut pas être négatif");
        }
        this.pv = Math.min(this.pvMax, this.pv + soin);
    }

    public boolean estKO() {
        return this.pv == 0;
    }

    public String getNom() { return nom; }
    public int getLevel() { return niveau; }
    public int getPv() { return pv; }
    public int getPvMax() { return pvMax; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getVit() { return vit; }
}