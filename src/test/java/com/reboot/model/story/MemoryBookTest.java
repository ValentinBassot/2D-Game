package com.reboot.model.story;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.reboot.model.fighter.RobotType;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MemoryBookTest {

    private static final List<String> LINES = List.of(
            "TANK|a porté une grand-mère",
            "",
            "HEALER|a soigné une petite fille",
            "HEALER|a veillé un vieux monsieur",
            "INFIGHTER|a arrêté un voleur");

    @Test
    void parse_groupsTheMemoriesByType() {
        MemoryBook book = MemoryBook.parse(LINES);
        assertEquals(1, book.count(RobotType.TANK));
        assertEquals(2, book.count(RobotType.HEALER));
        assertEquals("a porté une grand-mère", book.pick(RobotType.TANK, new Random(1)));
    }

    @Test
    void pick_returnsAMemoryOfTheRightType_reproducibly() {
        MemoryBook book = MemoryBook.parse(LINES);
        String first = book.pick(RobotType.HEALER, new Random(42));
        assertEquals(first, book.pick(RobotType.HEALER, new Random(42)));
        assertTrue(List.of("a soigné une petite fille", "a veillé un vieux monsieur").contains(first));
    }

    @Test
    void parse_rejectsInvalidFiles() {
        assertThrows(IllegalArgumentException.class, () -> MemoryBook.parse(List.of("TANK sans séparateur")));
        assertThrows(IllegalArgumentException.class, () -> MemoryBook.parse(List.of("ROBOT|type inconnu")));
        assertThrows(IllegalArgumentException.class, () -> MemoryBook.parse(List.of("TANK|seulement un type")));
    }

    @Test
    void theShippedFile_hasMemoriesForEveryType() {
        MemoryBook book = MemoryBook.load(MemoryBook.DEFAULT_PATH);
        for (RobotType type : RobotType.values()) {
            assertTrue(book.count(type) >= 3);
        }
    }

    @Test
    void load_rejectsAMissingFile() {
        assertThrows(IllegalArgumentException.class, () -> MemoryBook.load("/com/reboot/data/absent.txt"));
    }
}
