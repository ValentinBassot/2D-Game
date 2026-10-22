package com.reboot.model.quest;

/** Vaincre le chef de NEXUS, au cœur du data center. */
public class BossQuest implements Quest {

    @Override
    public String getDescription() {
        return "Vaincre le chef de NEXUS";
    }

    @Override
    public int getProgress(QuestContext context) {
        return context.isBossDefeated() ? 1 : 0;
    }

    @Override
    public int getTarget(QuestContext context) {
        return 1;
    }
}
