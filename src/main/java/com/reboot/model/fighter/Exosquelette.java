package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;

public class Exosquelette extends Combattant {

    public static final DamageSkill OVERLOAD = new DamageSkill("Surcharge", 18);

    private static final int BASE_HP = 55;
    private static final int BASE_ATK = 11;
    private static final int BASE_DEF = 10;
    private static final int BASE_SPEED = 7;

    public Exosquelette(int level) {
        this("Exosquelette", level, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
    }

    protected Exosquelette(String name, int level, int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        super(name, level, baseHp, baseAtk, baseDef, baseSpeed);
        addSkill(STRIKE);
        addSkill(OVERLOAD);
    }

    @Override
    public boolean isReprogrammable() {
        return false;
    }
}
