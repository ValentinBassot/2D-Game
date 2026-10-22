package com.reboot.model.nexus;

/** Les paliers de la barre de recherche, du plus discret au plus traqué. */
public enum WantedTier {
    /** De 0 à 24 : NEXUS ignore le joueur. */
    UNKNOWN("Inconnu", 0),
    /** De 25 à 49 : le joueur est repéré. */
    SPOTTED("Repéré", 25),
    /** De 50 à 74 : le joueur est traqué. */
    HUNTED("Traqué", 50),
    /** De 75 à 100 : le joueur est l'ennemi public numéro un. */
    PUBLIC_ENEMY("Ennemi public", 75);

    private final String label;
    private final int threshold;

    WantedTier(String label, int threshold) {
        this.label = label;
        this.threshold = threshold;
    }

    /**
     * Donne le palier correspondant à une valeur de la barre.
     *
     * @param value une valeur entre 0 et 100
     * @return le palier le plus haut atteint
     */
    public static WantedTier of(int value) {
        WantedTier reached = UNKNOWN;
        for (WantedTier tier : values()) {
            if (value >= tier.threshold) {
                reached = tier;
            }
        }
        return reached;
    }

    /** @return le nom du palier, affiché au joueur */
    public String getLabel() { return label; }

    /** @return la valeur minimale du palier */
    public int getThreshold() { return threshold; }
}
