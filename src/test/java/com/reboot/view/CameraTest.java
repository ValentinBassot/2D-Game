package com.reboot.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CameraTest {

    @Test
    void la_camera_centre_la_cible_au_milieu_du_monde() {
        assertEquals(400, Camera.offset(500, 200, 1000));
    }

    @Test
    void la_camera_ne_sort_pas_du_monde_au_debut() {
        assertEquals(0, Camera.offset(50, 200, 1000));
    }

    @Test
    void la_camera_ne_sort_pas_du_monde_a_la_fin() {
        assertEquals(800, Camera.offset(990, 200, 1000));
    }

    @Test
    void un_monde_plus_petit_que_la_vue_est_centre() {
        assertEquals(-50, Camera.offset(10, 200, 100));
        assertEquals(0, Camera.offset(10, 200, 200));
    }
}
