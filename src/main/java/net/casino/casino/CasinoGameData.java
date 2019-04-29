package net.casino.casino;

public interface CasinoGameData {

    /**
     * Get the name of the Casino game.
     * @return Casino game name.
     */
    String getGameName();

    /**
     * Get the current version of a Casino game.
     * @return Casino game version.
     */
    double getGameVersion();

    /**
     * Get the author of the Casino game.
     * @return Casino game author.
     */
    String getGameAuthor();
}
