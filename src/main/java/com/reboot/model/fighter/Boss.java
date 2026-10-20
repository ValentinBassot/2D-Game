package com.reboot.model.fighter;

public class Boss extends Exosquelette {

    public static final int FIXED_LEVEL = 10;

    private static final int BASE_HP = 80;
    private static final int BASE_ATK = 13;
    private static final int BASE_DEF = 12;
    private static final int BASE_SPEED = 8;

    public Boss() {
        super("Chef de NEXUS", FIXED_LEVEL, BASE_HP, BASE_ATK, BASE_DEF, BASE_SPEED);
    }

    @Override
    public boolean endsTheGame() {
        return true;
    }
}
