package cz.cuni.mff.pijalekj.ui;

import cz.cuni.mff.pijalekj.Game;

import java.io.Console;
import java.io.IOException;
import java.util.List;

public class TerminalUI implements EventUI {
    Game game;

    public TerminalUI(Game game) throws IOException {
        this.game = game;

    }

    @Override
    public void askPlayerName() throws IOException {
    }

    @Override
    public void showOptions(List<String> options) {
        int counter = 0;
        for (String item : options) {
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


    }

    public void anyKey() {

    }
}
