package com.reboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypeRobotTest {

    @Test
    void leTriangleDesFaiblesses_estRespecte() {
        assertTrue(TypeRobot.TANK.bat(TypeRobot.INFIGHTER));
        assertTrue(TypeRobot.INFIGHTER.bat(TypeRobot.HEALER));
        assertTrue(TypeRobot.HEALER.bat(TypeRobot.TANK));
    }

    @Test
    void lAvantageNeMarcheQueDansUnSens() {
        assertFalse(TypeRobot.INFIGHTER.bat(TypeRobot.TANK));
        assertFalse(TypeRobot.HEALER.bat(TypeRobot.INFIGHTER));
        assertFalse(TypeRobot.TANK.bat(TypeRobot.HEALER));
    }

    @Test
    void unTypeNeSeBatPasLuiMeme() {
        for (TypeRobot type : TypeRobot.values()) {
            assertFalse(type.bat(type));
        }
    }

    @Test
    void avecAvantage_leMultiplicateurVaut1Virgule5() {
        assertEquals(1.5, TypeRobot.TANK.multiplicateurContre(TypeRobot.INFIGHTER));
    }

    @Test
    void sansAvantage_leMultiplicateurVaut1_etPas0() {
        assertEquals(1.0, TypeRobot.TANK.multiplicateurContre(TypeRobot.HEALER));
        assertEquals(1.0, TypeRobot.TANK.multiplicateurContre(TypeRobot.TANK));
    }
}