package com.reboot.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.reboot.model.fighter.Boss;
import com.reboot.model.fighter.Combattant;
import com.reboot.model.fighter.Exosquelette;
import com.reboot.model.fighter.HealerBot;
import com.reboot.model.fighter.InfighterBot;
import com.reboot.model.fighter.Robot;
import com.reboot.model.fighter.TankBot;
import com.reboot.model.skill.Skill;
import com.reboot.model.team.Team;
import java.util.Random;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * Vérifie l'équilibrage calculé pour la progression : une équipe qui a suivi les quêtes
 * bat les chefs, une équipe trop faible perd. Chaque robot joue sa meilleure attaque, l'ennemi joue avec
 * l'IA du jeu ({@link RandomStrategy}) et chaque combat est rejoué sur plusieurs graines.
 */
class BossBalanceTest {

    private static final int SEEDS = 200;

    private static Team team(int infighter, int healer, int tank) {
        InfighterBot first = new InfighterBot(infighter);
        HealerBot second = new HealerBot(healer);
        TankBot third = new TankBot(tank);
        for (Robot robot : new Robot[] {first, second, third}) {
            robot.reprogram();
        }
        Team team = new Team(first);
        team.add(second);
        team.add(third);
        return team;
    }

    private static Skill bestAttack(Robot robot) {
        return switch (robot.getType()) {
            case INFIGHTER -> InfighterBot.FLURRY;
            case HEALER -> HealerBot.CORROSIVE_NANOBOTS;
            case TANK -> TankBot.CRUSH;
        };
    }

    private static CombatOutcome fight(Team team, Combattant enemy, long seed) {
        Combat combat = new Combat(team, enemy, new RandomStrategy(new Random(seed)));
        while (!combat.isOver()) {
            if (combat.mustSwitch()) {
                combat.switchTo(combat.getSwitchOptions().get(0));
            } else {
                Robot active = team.getRobots().stream().filter(r -> r == combat.getPlayer()).findFirst().orElseThrow();
                combat.useSkill(bestAttack(active));
            }
        }
        return combat.getOutcome();
    }

    private static void assertAlways(CombatOutcome expected, Supplier<Team> team, Supplier<Combattant> enemy) {
        for (long seed = 0; seed < SEEDS; seed++) {
            assertEquals(expected, fight(team.get(), enemy.get(), seed), "graine " + seed);
        }
    }

    @Test
    void aTeamThatFollowedTheQuests_beatsTheNexusChief() {
        assertAlways(CombatOutcome.VICTORY, () -> team(8, 7, 7), Boss::new);
    }

    @Test
    void aTeamThatSkippedTheQuests_losesAgainstTheNexusChief() {
        assertAlways(CombatOutcome.DEFEAT, () -> team(5, 5, 5), Boss::new);
    }

    @Test
    void theZoneOneTeam_beatsItsChief_butNotTheBoss() {
        assertAlways(CombatOutcome.VICTORY, () -> team(4, 4, 3), () -> new Exosquelette(4));
        assertAlways(CombatOutcome.DEFEAT, () -> team(4, 4, 3), Boss::new);
    }
}
