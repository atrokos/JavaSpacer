package cz.cuni.mff.pijalekj.ui;

import java.io.IOException;
import java.util.List;

public interface EventUI {
    void askPlayerName() throws IOException;
    void showOptions(List<String> options);
    void showBuySellScreen();
    void showBuyShipScreen();
    void showPlanetsScreen();
    void showMainScreen();
    void showSettings();
    void showWelcomeScreen();
}
