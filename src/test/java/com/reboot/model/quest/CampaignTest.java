package com.reboot.model.quest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CampaignTest {

    private final Campaign campaign = new Campaign();
    private final FakeContext context = new FakeContext();

    @Test
    void atTheStart_onlyTheHomeAndTheResidentialZoneAreOpen() {
        assertTrue(campaign.isUnlocked(Campaign.HOME, context));
        assertTrue(campaign.isUnlocked(Campaign.RESIDENTIAL, context));
        assertFalse(campaign.isUnlocked(Campaign.INDUSTRIAL, context));
        assertFalse(campaign.isUnlocked(Campaign.DATA_CENTER, context));
        assertEquals("Débloquer la Zone industrielle", campaign.current(context).orElseThrow().getDescription());
    }

    @Test
    void theChief_waitsForTheOtherQuests() {
        assertFalse(campaign.canChallengeChief(Campaign.RESIDENTIAL, context));
        context.completeResidentialPrerequisites();
        assertTrue(campaign.canChallengeChief(Campaign.RESIDENTIAL, context));
        assertFalse(campaign.isUnlocked(Campaign.INDUSTRIAL, context));
    }

    @Test
    void beatingTheChief_opensTheNextZone() {
        context.completeResidentialPrerequisites();
        context.chiefs.add(Campaign.RESIDENTIAL);
        assertTrue(campaign.isUnlocked(Campaign.INDUSTRIAL, context));
        assertEquals("Débloquer le Data center", campaign.current(context).orElseThrow().getDescription());
    }

    @Test
    void theDataCenter_opensAfterTheIndustrialChief() {
        context.wins.put(Campaign.INDUSTRIAL, Campaign.WINS_PER_ZONE);
        context.teamSize = 3;
        context.teamLevel = Campaign.INDUSTRIAL_TEAM_LEVEL;
        assertTrue(campaign.canChallengeChief(Campaign.INDUSTRIAL, context));
        context.chiefs.add(Campaign.INDUSTRIAL);
        assertTrue(campaign.isUnlocked(Campaign.DATA_CENTER, context));
    }

    @Test
    void theBoss_waitsForTheGuards() {
        context.guards = Campaign.MAIN_GUARDS;
        context.wins.put(Campaign.DATA_CENTER, 2);
        assertFalse(campaign.canChallengeBoss(context));
        context.wins.put(Campaign.DATA_CENTER, 3);
        assertTrue(campaign.canChallengeBoss(context));
    }

    @Test
    void theWantedBar_choosesTheDataCenterEntrance() {
        assertEquals(Campaign.DISCREET_GUARDS, Campaign.guardsFor(74));
        assertEquals(Campaign.MAIN_GUARDS, Campaign.guardsFor(75));
    }

    @Test
    void theCampaign_isOverOnceTheBossIsBeaten() {
        context.completeResidentialPrerequisites();
        context.chiefs.add(Campaign.RESIDENTIAL);
        context.wins.put(Campaign.INDUSTRIAL, Campaign.WINS_PER_ZONE);
        context.teamLevel = Campaign.INDUSTRIAL_TEAM_LEVEL;
        context.chiefs.add(Campaign.INDUSTRIAL);
        context.wins.put(Campaign.DATA_CENTER, 1);
        context.boss = true;
        assertTrue(campaign.current(context).isEmpty());
    }
}
