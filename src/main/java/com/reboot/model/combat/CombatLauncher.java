package com.reboot.model.combat;

import com.reboot.model.fighter.Combattant;

/**
 * Sépare l'exploration du combat. La carte demande un combat, elle n'a pas besoin de savoir comment il se
 * passe.
 */
public interface CombatLauncher {

    /**
     * Lance un combat contre un ennemi.
     *
     * @param enemy l'ennemi à affronter
     */
    void launch(Combattant enemy);
}
