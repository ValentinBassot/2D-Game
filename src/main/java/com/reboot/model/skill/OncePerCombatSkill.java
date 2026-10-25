package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;
import java.util.Objects;

/**
 * Limite une compétence à un seul usage par combat, comme la Surcharge des exosquelettes. Elle garde le nom,
 * la priorité et l'effet de la compétence qu'elle enveloppe (pattern Decorator). C'est
 * {@link com.reboot.model.combat.Combat} qui retient qu'elle a déjà servi.
 */
public class OncePerCombatSkill implements Skill {

    private final Skill skill;

    /**
     * Enveloppe une compétence.
     *
     * @param skill la compétence à limiter
     */
    public OncePerCombatSkill(Skill skill) {
        this.skill = Objects.requireNonNull(skill);
    }

    @Override
    public String apply(Combattant user, Combattant target) {
        return skill.apply(user, target);
    }

    @Override
    public String getName() { return skill.getName(); }

    @Override
    public boolean hasPriority() { return skill.hasPriority(); }

    @Override
    public boolean isOncePerCombat() { return true; }
}
