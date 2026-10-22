package com.reboot.model.fighter;

/**
 * État d'un robot K.O. en réparation. Il revient après {@value #VICTORIES_TO_REPAIR} victoires de l'équipe,
 * une de moins par pièce dépensée.
 */
public class RepairingState implements RobotState {

    /** Le nombre de victoires nécessaires pour réparer un robot. */
    public static final int VICTORIES_TO_REPAIR = 3;

    private final int victoriesLeft;

    /** Crée une réparation qui commence, avec le délai complet. */
    public RepairingState() {
        this(VICTORIES_TO_REPAIR);
    }

    private RepairingState(int victoriesLeft) {
        this.victoriesLeft = victoriesLeft;
    }

    @Override
    public int getVictoriesLeft() {
        return victoriesLeft;
    }

    @Override
    public boolean canFightForPlayer() {
        return false;
    }

    @Override
    public boolean isReprogrammable() {
        return false;
    }

    @Override
    public boolean isUnderRepair() {
        return true;
    }

    @Override
    public RobotState onReprogram() {
        throw new IllegalStateException("A robot under repair cannot be reprogrammed");
    }

    @Override
    public RobotState onKnockOut() {
        return this;
    }

    @Override
    public RobotState onTeamVictory() {
        return countDown();
    }

    @Override
    public RobotState onPartUsed() {
        return countDown();
    }

    private RobotState countDown() {
        int left = victoriesLeft - 1;
        return left <= 0 ? new AllyState() : new RepairingState(left);
    }
}
