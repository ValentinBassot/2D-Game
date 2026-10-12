package com.reboot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PlayerTest {

    private final TileMap map = TileMap.parse(List.of(
            "####",
            "#@.#",
            "#S.#",
            "####"));

    private Player atSpawn() {
        return new Player(map.getSpawnX(), map.getSpawnY());
    }

    @Test
    void le_joueur_regarde_vers_le_bas_au_depart() {
        assertEquals(Direction.DOWN, atSpawn().getFacing());
    }

    @Test
    void move_avance_d_une_case_sur_du_sol() {
        Player player = atSpawn();
        assertTrue(player.move(Direction.RIGHT, map));
        assertEquals(2, player.getX());
        assertEquals(1, player.getY());
        assertEquals(Direction.RIGHT, player.getFacing());
    }

    @Test
    void move_contre_un_mur_tourne_le_joueur_sans_le_deplacer() {
        Player player = atSpawn();
        assertFalse(player.move(Direction.UP, map));
        assertEquals(1, player.getX());
        assertEquals(1, player.getY());
        assertEquals(Direction.UP, player.getFacing());
    }

    @Test
    void move_ne_traverse_pas_le_poste() {
        Player player = atSpawn();
        assertFalse(player.move(Direction.DOWN, map));
        assertEquals(1, player.getY());
    }

    @Test
    void move_enchaine_plusieurs_cases() {
        Player player = atSpawn();
        player.move(Direction.RIGHT, map);
        assertTrue(player.move(Direction.DOWN, map));
        assertFalse(player.move(Direction.DOWN, map));
        assertEquals(2, player.getX());
        assertEquals(2, player.getY());
    }
}
