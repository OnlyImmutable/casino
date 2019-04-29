package net.casino.casino;

public abstract class CasinoGame implements CasinoGameData {

    /**
     * Called when the server starts up to initalise variables etc.
     */
    public abstract void onGameEnable();

    /**
     * Called on server shutdown or whenever a game is disabled using
     * The CasinoManager#disableCasinoGame(game); method.
     */
    public abstract void onGameDisable();
}
