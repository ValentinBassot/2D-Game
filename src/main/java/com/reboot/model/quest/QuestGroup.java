package com.reboot.model.quest;

import java.util.List;

/**
 * Un chapitre regroupe plusieurs quêtes et c'est lui-même une quête (pattern Composite). Il est fini quand
 * toutes ses quêtes le sont.
 *
 * <p>On a pris une convention simple, la dernière quête est toujours le combat contre le gardien (chef de
 * zone ou boss), et il ne s'ouvre qu'une fois les autres quêtes faites.
 */
public class QuestGroup implements Quest {

    private final String title;
    private final List<Quest> quests;

    /**
     * Crée un chapitre à partir de ses quêtes.
     *
     * @param title le titre du chapitre
     * @param quests ses quêtes, le combat contre le gardien en dernier
     */
    public QuestGroup(String title, List<Quest> quests) {
        if (quests.isEmpty()) {
            throw new IllegalArgumentException("A chapter needs at least one quest");
        }
        this.title = title;
        this.quests = List.copyOf(quests);
    }

    @Override
    public String getDescription() {
        return title;
    }

    @Override
    public int getProgress(QuestContext context) {
        return (int) quests.stream().filter(quest -> quest.isDone(context)).count();
    }

    @Override
    public int getTarget(QuestContext context) {
        return quests.size();
    }

    /**
     * {@return {@code true} si toutes les quêtes sont faites, à part le combat contre le gardien}
     *
     * @param context la partie en cours
     */
    public boolean isGuardianOpen(QuestContext context) {
        return quests.subList(0, quests.size() - 1).stream().allMatch(quest -> quest.isDone(context));
    }

    /** {@return les quêtes du chapitre, dans l'ordre} */
    public List<Quest> getQuests() {
        return quests;
    }
}
