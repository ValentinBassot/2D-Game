package com.reboot.model.game;

/** L'état d'une partie. */
public enum GameStatus {
    /** La partie continue. */
    IN_PROGRESS,
    /** Le chef de NEXUS est vaincu. */
    WON,
    /** Tous les robots sont en réparation. */
    LOST
}
