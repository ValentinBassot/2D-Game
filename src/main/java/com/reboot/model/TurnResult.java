package com.reboot.model;

import java.util.List;

public record TurnResult(List<String> log, boolean combatOver) {

    public TurnResult {
        log = List.copyOf(log);
    }
}