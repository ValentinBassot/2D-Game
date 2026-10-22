package com.reboot.model.quest;

/**
 * Atteindre un niveau d'équipe (la somme des niveaux des robots de l'équipe).
 *
 * <p>C'est la quête qui pousse au mélange : achever fait monter le niveau des robots rencontrés,
 * reprogrammer fait entrer ces robots plus forts dans l'équipe.
 */
public class TeamLevelQuest implements Quest {

    private final int target;

    /** @param target le niveau d'équipe à atteindre */
    public TeamLevelQuest(int target) {
        this.target = target;
    }

    @Override
    public String getDescription() {
        return "Atteindre un niveau d'équipe de " + target;
    }

    @Override
    public int getProgress(QuestContext context) {
        return context.getTeamLevel();
    }

    @Override
    public int getTarget(QuestContext context) {
        return target;
    }
}
