package com.reboot.model;

public class Skill {

    private final String name;
    private final int power;
    private final boolean ignoresDefense;

    public Skill(String name, int power, boolean ignoresDefense) {
        if (power < 0) {
            throw new IllegalArgumentException("Power cannot be negative");
        }
        this.name = name;
        this.power = power;
        this.ignoresDefense = ignoresDefense;
    }

    public String getName() { return name; }
    public int getPower() { return power; }
    public boolean ignoresDefense() { return ignoresDefense; }
}