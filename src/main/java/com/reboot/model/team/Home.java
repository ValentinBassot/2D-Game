package com.reboot.model.team;

import com.reboot.model.fighter.Robot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La Home de Léon : elle accueille les robots reprogrammés qui n'ont pas de place dans l'équipe.
 */
public class Home {

    private final List<Robot> stored = new ArrayList<>();

    /**
     * Range un robot allié à la Home.
     *
     * @param robot le robot à ranger, qui doit déjà être reprogrammé
     */
    public void store(Robot robot) {
        if (robot.isReprogrammable()) {
            throw new IllegalArgumentException("A hijacked robot cannot live at the Home");
        }
        if (stored.contains(robot)) {
            throw new IllegalArgumentException(robot.getName() + " is already at the Home");
        }
        stored.add(robot);
    }

    /**
     * Échange un robot de l'équipe avec un robot de la Home.
     *
     * @param team l'équipe du joueur
     * @param fromTeam le robot qui rentre à la Home
     * @param fromHome le robot qui rejoint l'équipe
     */
    public void swap(Team team, Robot fromTeam, Robot fromHome) {
        int index = stored.indexOf(fromHome);
        if (index < 0) {
            throw new IllegalArgumentException(fromHome.getName() + " is not at the Home");
        }
        team.replace(fromTeam, fromHome);
        stored.set(index, fromTeam);
    }

    /** @return les robots rangés, en lecture seule */
    public List<Robot> getStored() {
        return Collections.unmodifiableList(stored);
    }
}
