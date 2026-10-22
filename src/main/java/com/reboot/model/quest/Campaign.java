package com.reboot.model.quest;

import java.util.List;
import java.util.Optional;

/**
 * La progression de Reboot : trois chapitres, un par zone de combat.
 *
 * <p>Les seuils viennent d'une simulation de l'économie du jeu : avec ces valeurs, mélanger
 * « achever » et « reprogrammer » termine le quartier en 12 victoires environ, contre 31 en
 * reprogrammant toujours ; en achevant toujours, l'équipe ne dépasse jamais un robot.
 */
public final class Campaign {

    /** La Home de Léon. */
    public static final int HOME = 0;
    /** Le Quartier résidentiel, ouvert dès le départ. */
    public static final int RESIDENTIAL = 1;
    /** La Zone industrielle. */
    public static final int INDUSTRIAL = 2;
    /** Le data center de NEXUS. */
    public static final int DATA_CENTER = 3;

    /** Sous cette valeur de la barre de recherche, on entre au data center par l'entrée discrète. */
    public static final int DISCREET_ENTRY_LIMIT = 75;
    /** Les gardes à vaincre par l'entrée discrète. */
    public static final int DISCREET_GUARDS = 1;
    /** Les gardes à vaincre par l'entrée principale. */
    public static final int MAIN_GUARDS = 3;

    /** Les victoires demandées dans le quartier et dans la zone industrielle. */
    public static final int WINS_PER_ZONE = 8;
    /** Le niveau d'équipe demandé pour ouvrir la zone industrielle. */
    public static final int RESIDENTIAL_TEAM_LEVEL = 11;
    /** Le niveau d'équipe demandé pour ouvrir le data center. */
    public static final int INDUSTRIAL_TEAM_LEVEL = 20;

    private final List<QuestGroup> chapters = List.of(
            new QuestGroup("Débloquer la Zone industrielle", List.of(
                    new WinsQuest(RESIDENTIAL, "le Quartier résidentiel", WINS_PER_ZONE),
                    new TeamSizeQuest(3),
                    new TeamLevelQuest(RESIDENTIAL_TEAM_LEVEL),
                    new ChiefQuest(RESIDENTIAL))),
            new QuestGroup("Débloquer le Data center", List.of(
                    new WinsQuest(INDUSTRIAL, "la Zone industrielle", WINS_PER_ZONE),
                    new TeamSizeQuest(3),
                    new TeamLevelQuest(INDUSTRIAL_TEAM_LEVEL),
                    new ChiefQuest(INDUSTRIAL))),
            new QuestGroup("Libérer Néo-Lyon", List.of(
                    new GuardsQuest(DATA_CENTER),
                    new BossQuest())));

    /**
     * Le nombre de gardes du data center, fixé par la barre de recherche à l'entrée.
     *
     * @param wanted la valeur de la barre de recherche
     * @return {@value #DISCREET_GUARDS} sous {@value #DISCREET_ENTRY_LIMIT}, {@value #MAIN_GUARDS} sinon
     */
    public static int guardsFor(int wanted) {
        return wanted < DISCREET_ENTRY_LIMIT ? DISCREET_GUARDS : MAIN_GUARDS;
    }

    /** @return les chapitres, dans l'ordre */
    public List<QuestGroup> getChapters() {
        return chapters;
    }

    /**
     * @param zone le numéro d'une zone
     * @param context la partie en cours
     * @return {@code true} si le portail de cette zone est allumé
     */
    public boolean isUnlocked(int zone, QuestContext context) {
        return zone <= RESIDENTIAL || chapters.get(zone - INDUSTRIAL).isDone(context);
    }

    /**
     * @param zone le quartier ou la zone industrielle
     * @param context la partie en cours
     * @return {@code true} si le chef de cette zone accepte le combat
     */
    public boolean canChallengeChief(int zone, QuestContext context) {
        return chapters.get(zone - RESIDENTIAL).isGuardianOpen(context);
    }

    /**
     * @param context la partie en cours
     * @return {@code true} si le chef de NEXUS accepte le combat
     */
    public boolean canChallengeBoss(QuestContext context) {
        return chapters.get(DATA_CENTER - RESIDENTIAL).isGuardianOpen(context);
    }

    /**
     * @param context la partie en cours
     * @return le premier chapitre qui n'est pas terminé, ou rien si la campagne est finie
     */
    public Optional<QuestGroup> current(QuestContext context) {
        return chapters.stream().filter(chapter -> !chapter.isDone(context)).findFirst();
    }
}
