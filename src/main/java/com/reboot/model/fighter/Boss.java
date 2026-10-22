package com.reboot.model.fighter;

/** Le chef de NEXUS, boss final dans son exosquelette. Le vaincre termine la partie. */
public class Boss extends Exosquelette {

    /** Le niveau du chef de NEXUS, qui ne dépend pas du réseau. */
    public static final int FIXED_LEVEL = 10;

    private static final int BASE_HP = 80;
    private static final int BASE_ATK = 13;
    private static final int BASE_DEF = 12;
    private static final int BASE_SPEED = 8;

    /** Crée le chef de NEXUS, au niveau {@value #FIXED_LEVEL}. */
    public Boss() {
        super("Chef de NEXUS", FIXED_LEVEL, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
    }

    @Override
    public String getSpriteId() {
        return "chef_nexus";
    }

    @Override
    public boolean endsTheGame() {
        return true;
    }
}
