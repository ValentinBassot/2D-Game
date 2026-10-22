package com.reboot.model.quest;

/** Un objectif à remplir, avec sa progression. */
public interface Quest {

    /** @return ce qu'il faut faire, affiché au joueur */
    String getDescription();

    /**
     * @param context la partie en cours
     * @return où en est le joueur
     */
    int getProgress(QuestContext context);

    /**
     * @param context la partie en cours
     * @return la valeur à atteindre
     */
    int getTarget(QuestContext context);

    /**
     * @param context la partie en cours
     * @return {@code true} si la progression a atteint la cible
     */
    default boolean isDone(QuestContext context) {
        return getProgress(context) >= getTarget(context);
    }
}
