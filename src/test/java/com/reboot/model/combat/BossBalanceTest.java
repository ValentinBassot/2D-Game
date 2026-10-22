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
import org.junit.jupiter.api.Test;

/**
 * Vérifie l'équilibrage calculé pour la progression : une équipe qui a suivi les quêtes
 * bat les chefs, une équipe trop faible perd. Chaque robot joue sa meilleure attaque.
 */
class BossBalanceTest {

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

    private static CombatOutcome fight(Team team, Combattant enemy) {
        Combat combat = new Combat(team, enemy);
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

    @Test
    void aTeamThatFollowedTheQuests_beatsTheNexusChief() {
        assertEquals(CombatOutcome.VICTORY, fight(team(8, 7, 7), new Boss()));
    }

    @Test
    void aTeamThatSkippedTheQuests_losesAgainstTheNexusChief() {
        assertEquals(CombatOutcome.DEFEAT, fight(team(5, 5, 5), new Boss()));
    }

    @Test
    void theZoneOneTeam_beatsItsChief_butNotTheBoss() {
        assertEquals(CombatOutcome.VICTORY, fight(team(4, 4, 3), new Exosquelette(4)));
        assertEquals(CombatOutcome.DEFEAT, fight(team(4, 4, 3), new Boss()));
    }
}
