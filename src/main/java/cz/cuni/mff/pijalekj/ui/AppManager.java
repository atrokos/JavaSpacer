package cz.cuni.mff.pijalekj.ui;

import cz.cuni.mff.pijalekj.Game;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.utils.WorldGenerator;

import java.io.Console;
import java.io.IOException;

public class AppManager {
    Game game = null;
    EventUI eventUI;

    public AppManager(int planetAmount) throws IOException {
        this.game = initializeGame(planetAmount);
        eventUI = new TerminalUI(this.game);
    }
    private Game initializeGame(int planetAmount) {
        EntityManager em = new EntityManager();
        LocationsManager lm = WorldGenerator.generateLocations(planetAmount);
        CriminalsManager cm = new CriminalsManager();

        WorldGenerator.generateEntities(lm, em, cm);
        WorldGenerator.populateWorld(lm, em);
        return new Game(lm, cm, em);
    }

    public void setPlayerName() throws IOException {
    }

    public void mainScreen() {
        eventUI.showMainScreen();
        eventUI.showOptions(Constants.strings.getList("Options.Welcome"));
    }

    public void run() {
        // TODO run the game until the end
    }
}
