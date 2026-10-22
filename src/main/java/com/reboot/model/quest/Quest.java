package com.reboot.model.quest;

/** Un objectif à remplir, avec sa progression. */
public interface Quest {

    /** {@return ce qu'il faut faire, affiché au joueur} */
    String getDescription();

    /**
     * {@return où en est le joueur}
     *
     * @param context la partie en cours
     */
    int getProgress(QuestContext context);

    /**
     * {@return la valeur à atteindre}
     *
     * @param context la partie en cours
     */
    int getTarget(QuestContext context);

    /**
     * {@return {@code true} si la progression a atteint la cible}
     *
     * @param context la partie en cours
     */
    default boolean isDone(QuestContext context) {
        return getProgress(context) >= getTarget(context);
    }
}
