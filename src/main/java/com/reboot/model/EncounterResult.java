package com.reboot.model;

public record EncounterResult(CombatOutcome outcome, Combattant enemy, boolean choiceAvailable) {
}
