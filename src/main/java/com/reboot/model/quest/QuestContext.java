package com.reboot.model.quest;

/**
 * Ce que les quêtes ont besoin de lire sur la partie.
 *
 * <p>Les quêtes dépendent de cette interface et non de la partie elle-même :
 * on les teste avec un contexte simple, et la partie n'est jamais couplée aux quêtes concrètes.
 */
public interface QuestContext {

    /**
     * @param zone le numéro d'une zone
     * @return le nombre de combats gagnés dans cette zone
     */
    int getWins(int zone);

    /** @return le nombre de robots dans l'équipe */
    int getTeamSize();

    /** @return le niveau d'équipe : la somme des niveaux des robots de l'équipe */
    int getTeamLevel();

    /**
     * @param zone le numéro d'une zone
     * @return {@code true} si le chef de cette zone a été vaincu
     */
    boolean isChiefDefeated(int zone);

    /** @return le nombre de gardes à vaincre dans le data center avant d'affronter le boss */
    int getGuardsRequired();

    /** @return {@code true} si le chef de NEXUS a été vaincu */
    boolean isBossDefeated();
}
