package com.reboot.model.quest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuestTest {

    private final FakeContext context = new FakeContext();

    @Test
    void winsQuest_countsTheWinsOfItsZoneOnly() {
        Quest quest = new WinsQuest(1, "le Quartier résidentiel", 8);
        context.wins.put(1, 5);
        context.wins.put(2, 9);
        assertEquals("Gagner 8 combats dans le Quartier résidentiel", quest.getDescription());
        assertEquals(5, quest.getProgress(context));
        assertFalse(quest.isDone(context));
        context.wins.put(1, 8);
        assertTrue(quest.isDone(context));
    }

    @Test
    void teamQuests_readTheTeamSizeAndLevel() {
        context.teamSize = 3;
        context.teamLevel = 10;
        assertTrue(new TeamSizeQuest(3).isDone(context));
        assertFalse(new TeamLevelQuest(11).isDone(context));
        assertEquals(10, new TeamLevelQuest(11).getProgress(context));
    }

    @Test
    void guardianQuests_areDoneOnceTheGuardianIsBeaten() {
        Quest chief = new ChiefQuest(1);
        assertEquals(0, chief.getProgress(context));
        context.chiefs.add(1);
        assertTrue(chief.isDone(context));
        assertFalse(new BossQuest().isDone(context));
        context.boss = true;
        assertTrue(new BossQuest().isDone(context));
    }

    @Test
    void guardsQuest_followsTheRequiredGuards_andNeverGoesAboveIt() {
        Quest guards = new GuardsQuest(3);
        context.guards = Campaign.MAIN_GUARDS;
        context.wins.put(3, 2);
        assertEquals("2 / 3", guards.getProgress(context) + " / " + guards.getTarget(context));
        context.wins.put(3, 5);
        assertEquals(3, guards.getProgress(context));
    }

    @Test
    void aGroup_isAQuestDone_whenAllItsQuestsAre() {
        QuestGroup group = new QuestGroup("Chapitre", List.of(new TeamSizeQuest(2), new ChiefQuest(1)));
        assertEquals(0, group.getProgress(context));
        assertEquals(2, group.getTarget(context));
        assertFalse(group.isGuardianOpen(context));

        context.teamSize = 2;
        assertEquals(1, group.getProgress(context));
        assertTrue(group.isGuardianOpen(context));
        assertFalse(group.isDone(context));

        context.chiefs.add(1);
        assertTrue(group.isDone(context));
    }

    @Test
    void anEmptyGroup_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new QuestGroup("Vide", List.of()));
    }
}
