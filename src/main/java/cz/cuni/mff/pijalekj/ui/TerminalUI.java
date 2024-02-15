package cz.cuni.mff.pijalekj.ui;

import cz.cuni.mff.pijalekj.Game;

import java.io.Console;
import java.util.List;

public class TerminalUI implements EventUI {
    Game game;
    Console console = System.console();

    public TerminalUI(Game game) {
        this.game = game;
    }

    @Override
    public void askPlayerName() {
        console.printf("Enter player name: ");
    }

    @Override
    public void showOptions(List<String> options) {
        int counter = 0;
        for (String item : options) {
            console.printf("%d) %s\n", counter, item);
            ++counter;
        }
    }

    @Override
    public void showBuySellScreen() {

    }

    @Override
    public void showBuyShipScreen() {

    }

    @Override
    public void showPlanetsScreen() {

    }

    @Override
    public void showMainScreen() {

    }

    @Override
    public void showSettings() {

    }

    @Override
    public void showWelcomeScreen() {
        console.printf();

    }

    public void anyKey() {
        console.printf("Press any key to continue...");
    }
}
