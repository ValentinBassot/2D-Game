package com.reboot.model.story;

import com.reboot.model.fighter.RobotType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Souvenirs des robots, affichés sur l'écran de choix. Ce sont les bonnes actions du robot avant que NEXUS
 * le pirate. On les lit dans un fichier texte, une ligne par souvenir au format {@code TYPE|texte}, donc on
 * peut en ajouter sans recompiler.
 */
public final class MemoryBook {

    /** Le fichier des souvenirs fourni avec le jeu. */
    public static final String DEFAULT_PATH = "/com/reboot/data/memories.txt";

    private final Map<RobotType, List<String>> memories;

    private MemoryBook(Map<RobotType, List<String>> memories) {
        this.memories = memories;
    }

    /**
     * Charge les souvenirs depuis une ressource du classpath.
     *
     * @param path le chemin de la ressource
     * @return le livre de souvenirs
     */
    public static MemoryBook load(String path) {
        InputStream stream = MemoryBook.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Memories not found: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parse(reader.lines().toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Lit des lignes au format {@code TYPE|texte}. Les lignes vides sont ignorées.
     *
     * @param lines les lignes à lire
     * @return le livre de souvenirs, avec au moins un souvenir par type de robot
     */
    public static MemoryBook parse(List<String> lines) {
        Map<RobotType, List<String>> memories = new EnumMap<>(RobotType.class);
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            int separator = line.indexOf('|');
            if (separator < 0) {
                throw new IllegalArgumentException("Missing '|' in: " + line);
            }
            RobotType type = RobotType.valueOf(line.substring(0, separator).trim());
            memories.computeIfAbsent(type, key -> new ArrayList<>()).add(line.substring(separator + 1).trim());
        }
        for (RobotType type : RobotType.values()) {
            if (!memories.containsKey(type)) {
                throw new IllegalArgumentException("No memory for " + type);
            }
        }
        return new MemoryBook(memories);
    }

    /**
     * Tire un souvenir au hasard pour un type de robot.
     *
     * @param type le type du robot vaincu
     * @param random le générateur, injecté pour que les tests soient reproductibles
     * @return un souvenir de ce type
     */
    public String pick(RobotType type, Random random) {
        List<String> ofType = memories.get(type);
        return ofType.get(random.nextInt(ofType.size()));
    }

    /**
     * {@return le nombre de souvenirs de ce type}
     *
     * @param type un type de robot
     */
    public int count(RobotType type) {
        return memories.get(type).size();
    }
}
