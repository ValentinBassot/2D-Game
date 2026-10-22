package com.reboot.model.quest;

/** Gagner un certain nombre de combats dans une zone. */
public class WinsQuest implements Quest {

    private final int zone;
    private final String zoneName;
    private final int target;

    /**
     * Crée la quête de victoires dans une zone.
     *
     * @param zone le numéro de la zone
     * @param zoneName le nom de la zone, avec son article (« le Quartier résidentiel »)
     * @param target le nombre de victoires à atteindre
     */
    public WinsQuest(int zone, String zoneName, int target) {
        this.zone = zone;
        this.zoneName = zoneName;
        this.target = target;
    }

    @Override
    public String getDescription() {
        return "Gagner " + target + " combats dans " + zoneName;
    }

    @Override
    public int getProgress(QuestContext context) {
        return context.getWins(zone);
    }

    @Override
    public int getTarget(QuestContext context) {
        return target;
    }
}
