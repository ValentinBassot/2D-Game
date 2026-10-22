package com.reboot.model.quest;

/**
 * Ce que les quêtes ont besoin de savoir sur la partie.
 *
 * <p>Les quêtes dépendent de cette interface et pas directement de {@code Game}. Du coup on peut les
 * tester avec un faux contexte, et la partie ne dépend d'aucune quête concrète.
 */
public interface QuestContext {

    /**
     * {@return le nombre de combats gagnés dans cette zone}
     *
     * @param zone le numéro d'une zone
     */
    int getWins(int zone);

    /** {@return le nombre de robots dans l'équipe} */
    int getTeamSize();

    /** {@return le niveau d'équipe, la somme des niveaux de ses robots} */
    int getTeamLevel();

    /**
     * {@return {@code true} si le chef de cette zone a été vaincu}
     *
     * @param zone le numéro d'une zone
     */
    boolean isChiefDefeated(int zone);

    /** {@return le nombre de gardes à vaincre dans le data center avant d'affronter le boss} */
    int getGuardsRequired();

    /** {@return {@code true} si le chef de NEXUS a été vaincu} */
    boolean isBossDefeated();
}
