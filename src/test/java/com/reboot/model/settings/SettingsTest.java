package com.reboot.model.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class SettingsTest {

    @Test
    void theDefaults_areTheSmallestWindow_normalBrightness_andWindowed() {
        Settings settings = new Settings();
        assertEquals(new WindowSize(960, 720), settings.getWindowSize());
        assertEquals(0, settings.getBrightness());
        assertFalse(settings.isFullscreen());
    }

    @Test
    void windowSizes_cycleInBothDirections() {
        Settings settings = new Settings();
        settings.cycleWindowSize(1);
        assertEquals("1200 x 900", settings.getWindowSize().label());
        settings.cycleWindowSize(-2);
        assertEquals(new WindowSize(1440, 1080), settings.getWindowSize());
    }

    @Test
    void brightness_staysWithinItsBounds() {
        Settings settings = new Settings();
        settings.adjustBrightness(20);
        assertEquals(Settings.MAX_BRIGHTNESS, settings.getBrightness());
        settings.adjustBrightness(-30);
        assertEquals(Settings.MIN_BRIGHTNESS, settings.getBrightness());
    }

    @Test
    void savedSettings_areReadBackIdentically() {
        Settings settings = new Settings();
        settings.cycleWindowSize(1);
        settings.setFullscreen(true);
        settings.adjustBrightness(-2);
        settings.getKeys().bind(Action.MOVE_UP, "W");

        Settings read = Settings.fromProperties(settings.toProperties());

        assertEquals(settings.getWindowSize(), read.getWindowSize());
        assertTrue(read.isFullscreen());
        assertEquals(-2, read.getBrightness());
        assertEquals("W", read.getKeys().keyOf(Action.MOVE_UP));
    }

    @Test
    void aDamagedFile_fallsBackToTheDefaults() {
        Properties damaged = new Properties();
        damaged.setProperty("window.size", "42");
        damaged.setProperty("display.brightness", "très lumineux");
        damaged.setProperty("key.MOVE_UP", "");

        Settings read = Settings.fromProperties(damaged);

        assertEquals(new WindowSize(960, 720), read.getWindowSize());
        assertEquals(0, read.getBrightness());
        assertEquals("Z", read.getKeys().keyOf(Action.MOVE_UP));
    }
}
