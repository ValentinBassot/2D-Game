package com.reboot.model.quest;

import java.util.List;

/**
 * Un chapitre : un groupe de quêtes, qui est lui-même une quête (pattern Composite).
 * Il est terminé quand toutes ses quêtes le sont, et sa progression compte les quêtes terminées.
 *
 * <p>Par convention, la dernière quête d'un chapitre est le combat contre son gardien
 * (chef de zone ou boss) : il ne s'ouvre qu'une fois les autres quêtes remplies.
 */
public class QuestGroup implements Quest {

    private final String title;
    private final List<Quest> quests;

    /**
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
     * @param context la partie en cours
     * @return {@code true} si toutes les quêtes sauf le combat contre le gardien sont remplies
     */
    public boolean isGuardianOpen(QuestContext context) {
        return quests.subList(0, quests.size() - 1).stream().allMatch(quest -> quest.isDone(context));
    }

    /** @return les quêtes du chapitre, dans l'ordre */
    public List<Quest> getQuests() {
        return quests;
    }
}
