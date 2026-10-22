package com.reboot.model.settings;

/** Les actions du joueur, chacune liée à une touche réassignable dans les options. */
public enum Action {
    /** Monter, sur la carte ou dans un menu. */
    MOVE_UP("Haut"),
    /** Descendre, sur la carte ou dans un menu. */
    MOVE_DOWN("Bas"),
    /** Aller à gauche, ou diminuer une option. */
    MOVE_LEFT("Gauche"),
    /** Aller à droite, ou augmenter une option. */
    MOVE_RIGHT("Droite"),
    /** Valider un choix. */
    CONFIRM("Valider"),
    /** Ouvrir le menu pause, ou revenir en arrière. */
    PAUSE("Pause / retour");

    private final String label;

    Action(String label) {
        this.label = label;
    }

    /** {@return le nom de l'action, affiché dans les options} */
    public String getLabel() { return label; }
}
