package net.casino.casino;

import java.util.ArrayList;
import java.util.List;

public class CasinoManager {

    private List<CasinoGame> enabledCasinoGames;

    public CasinoManager() {
        enabledCasinoGames = new ArrayList<>();
    }

    public boolean enableCasinoGame(CasinoGame game) {
        if (enabledCasinoGames.contains(game)) return false;
        game.onGameEnable();
        enabledCasinoGames.add(game);
        return true;
    }

    public boolean disableCasinoGame(CasinoGame game) {
        if (!enabledCasinoGames.contains(game)) return false;
        game.onGameDisable();
        enabledCasinoGames.remove(game);
        return true;
    }

    public CasinoGame getCasinoGame(Class<? extends CasinoGame> clazz) {
        for (CasinoGame game : enabledCasinoGames) {
            if (clazz == game.getClass()) {
                return game;
            }
        }
        return null;
    }

    public List<CasinoGame> getEnabledCasinoGames() {
        return enabledCasinoGames;
    }
}
