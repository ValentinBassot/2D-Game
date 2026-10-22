package com.reboot.model.quest;

/** Vaincre l'exosquelette qui garde une zone. */
public class ChiefQuest implements Quest {

    private final int zone;

    /**
     * Crée la quête qui demande de vaincre le chef d'une zone.
     *
     * @param zone le numéro de la zone gardée
     */
    public ChiefQuest(int zone) {
        this.zone = zone;
    }

    @Override
    public String getDescription() {
        return "Vaincre l'exosquelette chef de zone";
    }

    @Override
    public int getProgress(QuestContext context) {
        return context.isChiefDefeated(zone) ? 1 : 0;
    }

    @Override
    public int getTarget(QuestContext context) {
        return 1;
    }
}
