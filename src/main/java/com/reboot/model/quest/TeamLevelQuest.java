package com.reboot.model.quest;

/**
 * Quête de niveau d'équipe (somme des niveaux des robots de l'équipe).
 *
 * <p>C'est elle qui pousse à mélanger les deux choix. Achever augmente le niveau des robots rencontrés, et
 * reprogrammer permet de récupérer ces robots plus forts.
 */
public class TeamLevelQuest implements Quest {

    private final int target;

    /**
     * Crée la quête de niveau d'équipe.
     *
     * @param target le niveau d'équipe à atteindre
     */
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
