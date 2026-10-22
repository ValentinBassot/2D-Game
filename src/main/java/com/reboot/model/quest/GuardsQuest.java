package com.reboot.model.quest;

/**
 * Vaincre les gardes du data center. Leur nombre dépend de la barre de recherche à l'entrée :
 * peu de gardes par l'entrée discrète, beaucoup par l'entrée principale.
 */
public class GuardsQuest implements Quest {

    private final int zone;

    /** @param zone le numéro du data center */
    public GuardsQuest(int zone) {
        this.zone = zone;
    }

    @Override
    public String getDescription() {
        return "Vaincre les gardes du data center";
    }

    @Override
    public int getProgress(QuestContext context) {
        return Math.min(context.getWins(zone), getTarget(context));
    }

    @Override
    public int getTarget(QuestContext context) {
        return context.getGuardsRequired();
    }
}
