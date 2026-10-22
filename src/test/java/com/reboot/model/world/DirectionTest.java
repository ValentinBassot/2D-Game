package com.reboot.model.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void chaque_direction_avance_d_une_seule_case() {
        for (Direction direction : Direction.values()) {
            assertEquals(1, Math.abs(direction.getDx()) + Math.abs(direction.getDy()));
        }
    }

    @Test
    void haut_remonte_les_lignes_et_droite_avance_les_colonnes() {
        assertEquals(-1, Direction.UP.getDy());
        assertEquals(1, Direction.DOWN.getDy());
        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(1, Direction.RIGHT.getDx());
    }
}
