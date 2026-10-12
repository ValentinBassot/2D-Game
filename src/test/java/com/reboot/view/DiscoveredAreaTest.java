package com.reboot.view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DiscoveredAreaTest {

    @Test
    void rien_n_est_decouvert_au_depart() {
        DiscoveredArea area = new DiscoveredArea(4, 3);
        assertFalse(area.isDiscovered(0, 0));
        assertFalse(area.isDiscovered(3, 2));
    }

    @Test
    void reveal_decouvre_le_rectangle_bords_inclus_et_rien_d_autre() {
        DiscoveredArea area = new DiscoveredArea(5, 5);
        area.reveal(1, 1, 2, 3);
        assertTrue(area.isDiscovered(1, 1));
        assertTrue(area.isDiscovered(2, 3));
        assertFalse(area.isDiscovered(0, 1));
        assertFalse(area.isDiscovered(3, 3));
        assertFalse(area.isDiscovered(1, 4));
    }

    @Test
    void une_case_decouverte_le_reste() {
        DiscoveredArea area = new DiscoveredArea(5, 5);
        area.reveal(0, 0, 1, 1);
        area.reveal(3, 3, 4, 4);
        assertTrue(area.isDiscovered(0, 0));
        assertTrue(area.isDiscovered(4, 4));
    }

    @Test
    void reveal_ignore_ce_qui_depasse_de_la_carte() {
        DiscoveredArea area = new DiscoveredArea(3, 2);
        area.reveal(-5, -5, 10, 10);
        assertTrue(area.isDiscovered(0, 0));
        assertTrue(area.isDiscovered(2, 1));
    }

    @Test
    void hors_de_la_carte_rien_n_est_decouvert() {
        DiscoveredArea area = new DiscoveredArea(3, 2);
        area.reveal(0, 0, 2, 1);
        assertFalse(area.isDiscovered(-1, 0));
        assertFalse(area.isDiscovered(3, 0));
        assertFalse(area.isDiscovered(0, 2));
    }
}
