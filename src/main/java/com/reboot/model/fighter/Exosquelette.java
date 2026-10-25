package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;
import com.reboot.model.skill.OncePerCombatSkill;
import com.reboot.model.skill.Skill;

/**
 * Exosquelette piloté par un membre de NEXUS. Comme le pilote est humain, on ne peut pas le reprogrammer.
 * Les chefs de zone en sont.
 */
public class Exosquelette extends Combattant {

    /** Surcharge, l'attaque des exosquelettes (puissance 18), une seule fois par combat. */
    public static final Skill OVERLOAD = new OncePerCombatSkill(new DamageSkill("Surcharge", 18));

    private static final int BASE_HP = 55;
    private static final int BASE_ATK = 11;
    private static final int BASE_DEF = 10;
    private static final int BASE_SPEED = 7;

    /**
     * Crée l'exosquelette d'un chef de zone.
     *
     * @param level son niveau
     */
    public Exosquelette(int level) {
        this("Exosquelette", level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
    }

    /**
     * Crée un exosquelette aux stats choisies, pour les sous-classes comme le boss.
     *
     * @param name son nom
     * @param level son niveau
     * @param baseHp ses PV au niveau 1
     * @param baseAtk son attaque au niveau 1
     * @param baseDef sa défense au niveau 1
     * @param baseSpeed sa vitesse au niveau 1
     */
    protected Exosquelette(String name, int level, int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        super(name, level, baseHp, baseAtk, baseDef, baseSpeed);
        addSkill(STRIKE);
        addSkill(OVERLOAD);
    }

    @Override
    public boolean isReprogrammable() {
        return false;
    }

    @Override
    public String getSpriteId() {
        return "exosquelette";
    }
}
