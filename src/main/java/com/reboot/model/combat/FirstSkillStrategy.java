package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.skill.Skill;
import java.util.List;

/** Stratégie la plus simple : toujours la première compétence encore utilisable. */
public class FirstSkillStrategy implements CombatStrategy {

    /** Crée la stratégie. Elle n'a besoin d'aucun réglage. */
    public FirstSkillStrategy() {
    }

    @Override
    public Skill chooseSkill(Combattant attacker, List<Skill> usable, Combattant target) {
        return usable.get(0);
    }
}
