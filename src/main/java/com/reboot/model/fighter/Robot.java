package com.reboot.model.fighter;

import java.util.Locale;

/**
 * Un robot, c'est un combattant avec un type et un état (pattern State). Ennemi ou allié, c'est la même
 * classe, seul l'état change.
 */
public abstract class Robot extends Combattant {

    private final RobotType type;
    private RobotState state = new HijackedState();

    /**
     * Crée un robot détourné, qui connaît la frappe de base.
     *
     * @param name son nom
     * @param type son type
     * @param level son niveau
     * @param baseHp ses PV au niveau 1
     * @param baseAtk son attaque au niveau 1
     * @param baseDef sa défense au niveau 1
     * @param baseSpeed sa vitesse au niveau 1
     */
    protected Robot(String name, RobotType type, int level,
                    int baseHp, int baseAtk, int baseDef, int baseSpeed) {
        super(name, level, baseHp, baseAtk, baseDef, baseSpeed);
        this.type = type;
        addSkill(STRIKE);
    }

    /** {@return son type, qui décide des faiblesses} */
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

    /** {@return son état actuel} */
    public RobotState getState() {
        return state;
    }

    /** {@return {@code true} s'il peut se battre pour le joueur} */
    public boolean canFightForPlayer() {
        return state.canFightForPlayer();
    }

    @Override
    public boolean isReprogrammable() {
        return state.isReprogrammable();
    }

    /** {@return {@code true} s'il est en réparation} */
    public boolean isUnderRepair() {
        return state.isUnderRepair();
    }

    /** {@return le nombre de victoires avant la fin de sa réparation, ou 0 s'il n'est pas en réparation} */
    public int getRepairVictoriesLeft() {
        return state.getVictoriesLeft();
    }

    /**
     * Reprogramme le robot, qui devient l'allié du joueur.
     *
     * @throws IllegalStateException s'il n'est pas détourné
     */
    public void reprogram() {
        state = state.onReprogram();
    }

    /** À appeler quand le robot tombe K.O. Un allié part alors en réparation. */
    public void handleKnockOut() {
        state = state.onKnockOut();
    }

    /**
     * À appeler après une victoire de l'équipe. La réparation avance, et le robot récupère tous ses PV
     * quand elle est finie.
     */
    public void recordTeamVictory() {
        changeState(state.onTeamVictory());
    }

    /** Utilise une pièce sur ce robot, sa réparation avance d'une victoire. */
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
