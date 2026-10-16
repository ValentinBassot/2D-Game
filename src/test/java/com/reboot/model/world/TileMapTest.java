package com.reboot.model.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;

class TileMapTest {

    private static final List<String> ROWS = List.of(
            "#1#2#",
            "#.@~#",
            "#S..#",
            "#####");

    @Test
    void parse_lit_les_dimensions_et_le_point_de_depart() {
        TileMap map = TileMap.parse(ROWS);
        assertEquals(5, map.getWidth());
        assertEquals(4, map.getHeight());
        assertEquals(2, map.getSpawnX());
        assertEquals(1, map.getSpawnY());
    }

    @Test
    void parse_reconnait_chaque_symbole() {
        TileMap map = TileMap.parse(ROWS);
        assertEquals(Tile.WALL, map.tileAt(0, 0));
        assertEquals(Tile.PORTAL, map.tileAt(1, 0));
        assertEquals(Tile.GROUND, map.tileAt(1, 1));
        assertEquals(Tile.GROUND, map.tileAt(2, 1));
        assertEquals(Tile.ENCOUNTER, map.tileAt(3, 1));
        assertEquals(Tile.STATION, map.tileAt(1, 2));
    }

    @Test
    void sol_rencontre_et_portail_sont_franchissables_mais_pas_mur_ni_poste() {
        TileMap map = TileMap.parse(ROWS);
        assertTrue(map.isWalkable(1, 1));
        assertTrue(map.isWalkable(3, 1));
        assertTrue(map.isWalkable(1, 0));
        assertFalse(map.isWalkable(0, 0));
        assertFalse(map.isWalkable(1, 2));
    }

    @Test
    void hors_de_la_carte_c_est_un_mur() {
        TileMap map = TileMap.parse(ROWS);
        assertFalse(map.contains(-1, 0));
        assertFalse(map.contains(0, 4));
        assertEquals(Tile.WALL, map.tileAt(5, 0));
        assertFalse(map.isWalkable(0, -1));
    }

    @Test
    void portalZoneAt_donne_la_zone_du_portail_ou_NO_PORTAL() {
        TileMap map = TileMap.parse(ROWS);
        assertEquals(1, map.portalZoneAt(1, 0));
        assertEquals(2, map.portalZoneAt(3, 0));
        assertEquals(TileMap.NO_PORTAL, map.portalZoneAt(1, 1));
        assertEquals(TileMap.NO_PORTAL, map.portalZoneAt(-1, -1));
        assertEquals(0, TileMap.parse(List.of("0@")).portalZoneAt(0, 0));
    }

    @Test
    void parse_refuse_une_carte_invalide() {
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of()));
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of("")));
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of("#@#", "##")));
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of("#@?")));
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of("#.#")));
        assertThrows(IllegalArgumentException.class, () -> TileMap.parse(List.of("@@")));
    }

    @Test
    void load_lit_la_carte_de_la_home() {
        TileMap home = TileMap.load("/com/reboot/maps/home.txt");
        assertEquals(48, home.getWidth());
        assertEquals(36, home.getHeight());
        assertTrue(home.isWalkable(home.getSpawnX(), home.getSpawnY()));
    }

    @Test
    void load_lit_la_carte_de_la_zone_1_avec_son_portail_de_retour_et_de_l_herbe() {
        TileMap zone = TileMap.load("/com/reboot/maps/zone1.txt");
        assertEquals(0, zone.portalZoneAt(zone.getSpawnX(), zone.getSpawnY() - 1));
        boolean grass = false;
        for (int y = 0; y < zone.getHeight(); y++) {
            for (int x = 0; x < zone.getWidth(); x++) {
                grass |= zone.tileAt(x, y) == Tile.ENCOUNTER;
            }
        }
        assertTrue(grass);
    }

    @Test
    void load_refuse_un_fichier_absent() {
        assertThrows(IllegalArgumentException.class, () -> TileMap.load("/com/reboot/maps/absent.txt"));
    }
}
