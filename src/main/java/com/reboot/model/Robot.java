package com.reboot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Robot extends Combattant {

    public static final Skill FRAPPE = new Skill("Frappe", 10, false);

    private final TypeRobot type;
    private final List<Skill> competences = new ArrayList<>();

    protected Robot(String nom, TypeRobot type, int niveau,
                    int pvBase, int atkBase, int defBase, int vitBase) {
        super(nom, niveau, pvBase, atkBase, defBase, vitBase);
        this.type = type;
        this.competences.add(FRAPPE);
    }

    protected final void ajouterCompetence(Skill competence) {
        this.competences.add(competence);
    }

    public TypeRobot getType() {
        return type;
    }

    public List<Skill> getCompetences() {
        return Collections.unmodifiableList(competences);
    }
}