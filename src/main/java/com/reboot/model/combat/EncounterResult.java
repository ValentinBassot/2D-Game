package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;

public record EncounterResult(CombatOutcome outcome, Combattant enemy, boolean choiceAvailable) {
}
