package com.reboot;

import com.reboot.model.combat.CombatLauncher;
import com.reboot.model.combat.CombatOutcome;
import com.reboot.model.combat.Encounter;
import com.reboot.model.fighter.Boss;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.game.Game;
import com.reboot.model.story.MemoryBook;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.game.ChoiceResolver;
import com.reboot.model.world.EncounterTrigger;
import com.reboot.model.world.Player;
import com.reboot.model.world.Tile;
import com.reboot.model.world.TileMap;
import com.reboot.view.ChoiceView;
import com.reboot.view.CombatView;
import com.reboot.view.DiscoveredArea;
import com.reboot.view.EndView;
import com.reboot.view.ExplorationView;
import com.reboot.view.ScaledScreen;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameController implements CombatLauncher {

    private static final int HOME = 0;
    private static final int ENCOUNTER_ONE_IN = 6;

    private record ZoneSpec(String mapPath, String spritePrefix, int level) {
    }

    private static final Map<Integer, ZoneSpec> ZONES = Map.of(
            HOME, new ZoneSpec("/com/reboot/maps/home.txt", "home", 0),
            1, new ZoneSpec("/com/reboot/maps/zone1.txt", "z1", 2),
            3, new ZoneSpec("/com/reboot/maps/zone3.txt", "z3", 8));

    private final ScaledScreen screen;
    private final Random random = new Random();
    private final EncounterTrigger encounters = new EncounterTrigger(random, ENCOUNTER_ONE_IN);
    private final MemoryBook memories = MemoryBook.load(MemoryBook.DEFAULT_PATH);

    private final Map<Integer, DiscoveredArea> discovered = new HashMap<>();

    private Game game = Game.newGame();

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
            game.restAtHome();
        }
        DiscoveredArea seen = discovered.computeIfAbsent(zone,
                key -> new DiscoveredArea(map.getWidth(), map.getHeight()));
        exploration = new ExplorationView(map, arrival(from), spec.spritePrefix(), seen);
        exploration.setOnStep(this::onStep);
        exploration.setPortalOpen(ZONES::containsKey);
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
        if (tile == Tile.PORTAL && ZONES.containsKey(target)) {
            enterZone(target, currentZone);
        } else if (tile == Tile.BOSS) {
            launch(new Boss());
        } else if (encounters.shouldTrigger(tile)) {
            fightRobot(new HealerBot(ZONES.get(currentZone).level()));
        }
    }

    @Override
    public void launch(Combattant enemy) {
        startCombat(enemy, this::showExploration);
    }

    private void fightRobot(Robot robot) {
        startCombat(robot, () -> showChoice(robot));
    }

    private void startCombat(Combattant enemy, Runnable afterVictory) {
        exploration.stop();
        CombatView combat = new CombatView(new Encounter(game.getTeam(), enemy), result -> {
            game.onCombatEnd(result);
            if (game.isOver()) {
                showEnd();
            } else if (result.outcome() == CombatOutcome.DEFEAT) {
                enterZone(HOME, HOME);
            } else {
                afterVictory.run();
            }
        });
        screen.show(combat);
        combat.start();
    }

    private void showChoice(Robot robot) {
        String memory = memories.pick(robot.getType(), random);
        ChoiceView choice = new ChoiceView(robot, memory, new ChoiceResolver(game), this::showExploration);
        screen.show(choice);
        choice.start();
    }

    private void showEnd() {
        EndView end = new EndView(game.getStatus(), game.getTeam(), this::restart);
        screen.show(end);
        end.start();
    }

    private void restart() {
        game = Game.newGame();
        discovered.clear();
        enterZone(HOME, HOME);
    }
}
