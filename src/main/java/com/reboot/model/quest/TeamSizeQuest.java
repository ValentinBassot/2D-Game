package com.reboot.model.quest;

/** Quête qui demande un certain nombre de robots dans l'équipe, donc d'en reprogrammer. */
public class TeamSizeQuest implements Quest {

    private final int target;

    /**
     * Crée la quête de taille d'équipe.
     *
     * @param target le nombre de robots à atteindre
     */
    public TeamSizeQuest(int target) {
        this.target = target;
    }

    @Override
    public String getDescription() {
        return "Avoir " + target + " robots dans l'équipe";
    }

    @Override
    public int getProgress(QuestContext context) {
        return context.getTeamSize();
    }

    @Override
    public int getTarget(QuestContext context) {
        return target;
    }
}
