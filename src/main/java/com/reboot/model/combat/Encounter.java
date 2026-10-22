package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.team.Team;

public class Encounter {
    private final Combat combat;

    public Encounter(Combattant player, Combattant enemy) {
        this.combat = new Combat(player, enemy);
    }

    public Encounter(Team team, Combattant enemy) {
        this.combat = new Combat(team, enemy);
    }

    public Combat getCombat() {
        return combat;
    }

    public boolean isFinished() {
        return combat.isOver();
    }

    public EncounterResult getResult() {
        if (!isFinished()) {
            throw new IllegalStateException("The encounter is not finished yet");
        }
        CombatOutcome outcome = combat.getOutcome();
        boolean choiceAvailable = outcome == CombatOutcome.VICTORY && combat.getEnemy().isReprogrammable();
        return new EncounterResult(outcome, combat.getEnemy(), choiceAvailable);
    }
}
