package com.reboot.model.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class KeyBindingsTest {

    private final KeyBindings bindings = new KeyBindings();

    @Test
    void theDefaults_followTheGdd() {
        assertEquals("Z", bindings.keyOf(Action.MOVE_UP));
        assertEquals("Q", bindings.keyOf(Action.MOVE_LEFT));
        assertEquals(Optional.of(Action.MOVE_DOWN), bindings.actionOf("S"));
        assertEquals(Optional.of(Action.CONFIRM), bindings.actionOf("ENTER"));
    }

    @Test
    void arrows_alwaysWork_asAlternates() {
        bindings.bind(Action.MOVE_UP, "W");
        assertEquals(Optional.of(Action.MOVE_UP), bindings.actionOf("UP"));
        assertEquals(Optional.of(Action.MOVE_UP), bindings.actionOf("W"));
        assertEquals(Optional.of(Action.CONFIRM), bindings.actionOf("SPACE"));
    }

    @Test
    void rebinding_takesEffectImmediately() {
        bindings.bind(Action.MOVE_UP, "W");
        assertEquals("W", bindings.keyOf(Action.MOVE_UP));
        assertTrue(bindings.actionOf("Z").isEmpty());
    }

    @Test
    void aKeyAlreadyUsed_isSwapped() {
        bindings.bind(Action.MOVE_UP, "S");
        assertEquals("S", bindings.keyOf(Action.MOVE_UP));
        assertEquals("Z", bindings.keyOf(Action.MOVE_DOWN));
    }

    @Test
    void aPrimaryKey_winsOverAnAlternate() {
        bindings.bind(Action.CONFIRM, "UP");
        assertEquals(Optional.of(Action.CONFIRM), bindings.actionOf("UP"));
    }

    @Test
    void unknownKey_triggersNothing() {
        assertTrue(bindings.actionOf("F5").isEmpty());
    }

    @Test
    void resetToDefaults_restoresEveryKey() {
        bindings.bind(Action.MOVE_UP, "W");
        bindings.resetToDefaults();
        assertEquals("Z", bindings.keyOf(Action.MOVE_UP));
    }

    @Test
    void anEmptyKey_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> bindings.bind(Action.PAUSE, " "));
    }

    @Test
    void everyAction_hasALabelForTheOptions() {
        assertEquals("Haut", Action.MOVE_UP.getLabel());
        assertEquals("Pause / retour", Action.PAUSE.getLabel());
    }
}
