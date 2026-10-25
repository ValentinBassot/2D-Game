package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.skill.Skill;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * IA ennemie aléatoire : une compétence au hasard parmi celles que l'attaquant peut encore lancer. Le
 * {@code Random} est passé en paramètre pour que les tests soient reproductibles avec une graine fixe.
 */
public class RandomStrategy implements CombatStrategy {

    private final Random random;

    /**
     * Crée la stratégie.
     *
     * @param random la source de hasard
     */
    public RandomStrategy(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Skill chooseSkill(Combattant attacker, List<Skill> usable, Combattant target) {
        return usable.get(random.nextInt(usable.size()));
    }
}
