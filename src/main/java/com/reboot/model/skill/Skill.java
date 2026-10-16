package com.reboot.model.skill;

import com.reboot.model.fighter.Combattant;

public interface Skill {

    String getName();

    default boolean hasPriority() {
        return false;
    }

    String apply(Combattant user, Combattant target);
}
