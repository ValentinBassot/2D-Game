package com.reboot;

import com.reboot.model.CombatLauncher;
import com.reboot.model.CombatOutcome;
import com.reboot.model.Combattant;
import com.reboot.model.Encounter;
import com.reboot.model.EncounterResult;
import com.reboot.model.EncounterTrigger;
import com.reboot.model.HealerBot;
import com.reboot.model.InfighterBot;
import com.reboot.model.Player;
import com.reboot.model.Robot;
import com.reboot.model.Tile;
import com.reboot.model.TileMap;
import com.reboot.view.CombatView;
import com.reboot.view.ExplorationView;
import com.reboot.view.ScaledScreen;
import java.util.Map;
import java.util.Random;

public class GameController implements CombatLauncher {

    private static final int HOME = 0;
    private static final int ENCOUNTER_ONE_IN = 6;
    private static final int STARTER_LEVEL = 2;

    private record ZoneSpec(String mapPath, String spritePrefix, int level) {
    }

    private static final Map<Integer, ZoneSpec> ZONES = Map.of(
            HOME, new ZoneSpec("/com/reboot/maps/home.txt", "home", 0),
            1, new ZoneSpec("/com/reboot/maps/zone1.txt", "z1", 2));

    private final ScaledScreen screen;
    private final Robot partner = new InfighterBot(STARTER_LEVEL);
    private final EncounterTrigger encounters = new EncounterTrigger(new Random(), ENCOUNTER_ONE_IN);

    private int currentZone = HOME;
    private TileMap map;
    private ExplorationView exploration;

    public GameController(ScaledScreen screen) {
        this.screen = screen;
    }

    public void start() {
        enterZone(HOME, HOME);
    }

    private void enterZone(int zone, int from) {
        if (exploration != null) {
            exploration.stop();
        }
        ZoneSpec spec = ZONES.get(zone);
        currentZone = zone;
        map = TileMap.load(spec.mapPath());
        if (zone == HOME) {
            partner.heal(partner.getMaxHp());
        }
        exploration = new ExplorationView(map, arrival(from), spec.spritePrefix());
        exploration.setOnStep(this::onStep);
        showExploration();
    }

    private Player arrival(int from) {
        if (from != currentZone) {
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    if (map.portalZoneAt(x, y) == from && map.isWalkable(x, y + 1)) {
                        return new Player(x, y + 1);
                    }
                }
            }
        }
        return new Player(map.getSpawnX(), map.getSpawnY());
    }

    private void showExploration() {
        screen.show(exploration);
        exploration.start();
    }

    private void onStep(int x, int y) {
        Tile tile = map.tileAt(x, y);
        int target = map.portalZoneAt(x, y);
        if (tile == Tile.PORTAL && target <= ExplorationView.UNLOCKED_ZONE && ZONES.containsKey(target)) {
            enterZone(target, currentZone);
        } else if (encounters.shouldTrigger(tile)) {
            launch(new HealerBot(ZONES.get(currentZone).level()));
        }
    }

    @Override
    public void launch(Combattant enemy) {
        exploration.stop();
        CombatView combat = new CombatView(new Encounter(partner, enemy), this::onCombatEnd);
        screen.show(combat);
        combat.start();
    }

    private void onCombatEnd(EncounterResult result) {
        if (result.outcome() == CombatOutcome.DEFEAT) {
            enterZone(HOME, HOME);
        } else {
            showExploration();
        }
    }
}
