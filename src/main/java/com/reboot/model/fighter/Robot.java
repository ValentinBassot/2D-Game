package com.reboot.model.fighter;

import com.reboot.model.skill.DamageSkill;

public abstract class Robot extends Combattant {

    public static final DamageSkill STRIKE = new DamageSkill("Frappe", 10);

    private final RobotType type;
    private RobotState state = new HijackedState();

    protected Robot(String name, RobotType type, int level,
                    int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        super(name, level, baseHp, baseAtk, baseDef, baseSpeed);
        this.type = type;
        addSkill(STRIKE);
    }

    public RobotType getType() {
        return type;
    }

    public RobotState getState() {
        return state;
    }

    public boolean canFightForPlayer() {
        return state.canFightForPlayer();
    }

    public boolean isReprogrammable() {
        return state.isReprogrammable();
    }

    public void reprogram() {
        state = state.onReprogram();
    }

    public void handleKnockOut() {
        state = state.onKnockOut();
    }

    public void recordTeamVictory() {
        state = state.onTeamVictory();
    }

    public void useRepairPart() {
        state = state.onPartUsed();
    }
}
