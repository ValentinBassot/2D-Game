package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;
import com.reboot.model.skill.Skill;
import java.util.List;

/**
 * Cerveau d'un ennemi : choisit la compétence qu'il utilise à chaque tour. {@link Combat} demande à sa
 * stratégie sans savoir comment elle décide, on peut donc changer d'IA sans toucher au combat.
 */
public interface CombatStrategy {

    /**
     * Choisit la compétence de l'attaquant pour ce tour.
     *
     * @param attacker celui qui attaque
     * @param usable les compétences qu'il peut encore lancer dans ce combat, jamais vide
     * @param target sa cible
     * @return une compétence de {@code usable}
     */
    Skill chooseSkill(Combattant attacker, List<Skill> usable, Combattant target);
}
