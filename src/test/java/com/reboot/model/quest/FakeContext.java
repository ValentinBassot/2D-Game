package com.reboot.model.quest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Un contexte de quêtes réglable à la main, pour tester les quêtes sans lancer de partie. */
class FakeContext implements QuestContext {

    final Map<Integer, Integer> wins = new HashMap<>();
    final Set<Integer> chiefs = new HashSet<>();
    int teamSize = 1;
    int teamLevel = 2;
    int guards = Campaign.DISCREET_GUARDS;
    boolean boss;

    @Override
    public int getWins(int zone) {
        return wins.getOrDefault(zone, 0);
    }

    @Override
    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public int getTeamLevel() {
        return teamLevel;
    }

    @Override
    public boolean isChiefDefeated(int zone) {
        return chiefs.contains(zone);
    }

    @Override
    public int getGuardsRequired() {
        return guards;
    }

    @Override
    public boolean isBossDefeated() {
        return boss;
    }

    void completeResidentialPrerequisites() {
        wins.put(Campaign.RESIDENTIAL, Campaign.WINS_PER_ZONE);
        teamSize = 3;
        teamLevel = Campaign.RESIDENTIAL_TEAM_LEVEL;
    }
}
