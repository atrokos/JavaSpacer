package cz.cuni.mff.pijalekj.ui;

import java.util.List;

public interface EventUI {
    void askPlayerName();
    void showOptions(List<String> options);
    void showBuySellScreen();
    void showBuyShipScreen();
    void showPlanetsScreen();
    void showMainScreen();
    void showSettings();
    void showWelcomeScreen();
}
