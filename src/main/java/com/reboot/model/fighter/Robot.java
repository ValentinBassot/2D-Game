package com.reboot.model.fighter;

import java.util.Locale;

public abstract class Robot extends Combattant {

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

    @Override
    public String getSpriteId() {
        return type.name().toLowerCase(Locale.ROOT) + "bot";
    }

    @Override
    public double advantageAgainst(Combattant defender) {
        return defender.weaknessTo(type);
    }

    @Override
    protected double weaknessTo(RobotType attackerType) {
        return attackerType.multiplierAgainst(type);
    }

    public RobotState getState() {
        return state;
    }

    public boolean canFightForPlayer() {
        return state.canFightForPlayer();
    }

    @Override
    public boolean isReprogrammable() {
        return state.isReprogrammable();
    }

    public boolean isUnderRepair() {
        return state.isUnderRepair();
    }

    public int getRepairVictoriesLeft() {
        return state.getVictoriesLeft();
    }

    public void reprogram() {
        state = state.onReprogram();
    }

    public void handleKnockOut() {
        state = state.onKnockOut();
    }

    public void recordTeamVictory() {
        changeState(state.onTeamVictory());
    }

    public void useRepairPart() {
        changeState(state.onPartUsed());
    }

    private void changeState(RobotState next) {
        boolean repairFinished = state.isUnderRepair() && !next.isUnderRepair();
        state = next;
        if (repairFinished) {
            heal(getMaxHp());
        }
    }
}
