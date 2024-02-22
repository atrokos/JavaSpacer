package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.battle.Battle;
import cz.cuni.mff.pijalekj.entities.EntityStats;
import cz.cuni.mff.pijalekj.entities.GoodsPrices;
import cz.cuni.mff.pijalekj.entities.Player;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.utils.RunnableWException;
import org.fusesource.jansi.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Game  {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();
    private final ArrayList<Input.Option> mainMenuOptions= new ArrayList<>();
    private boolean continuePlay = true;

    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        this.locationsManager = lm;
        this.criminalsManager = cm;
        this.entityManager = em;
    }

    public void initGame() throws IOException {
        System.out.print("Welcome to SpaceTrader!\nEnter name: ");
        String name = Input.askString(String::isBlank);
    }

    public boolean play() throws IOException {
        if (this.clock.tick()) {
            this.entityManager.resetNPCs(this.criminalsManager, this.locationsManager);
            this.locationsManager.updateAllPlanets();
            this.locationsManager.bigCheck(this.entityManager.getEntities().length);
        }
        this.playerPlay();
        HashMap<Integer, Integer> fightRequests = this.entityManager.play();
        this.handleBattles(fightRequests);
        this.criminalsManager.updateCriminals();

        return this.entityManager.getPlayer().isAlive();
    }

    private void handleBattles(HashMap<Integer, Integer> fightRequests) {
        // Player's request always has the highest priority
        int playerOpponent = fightRequests.getOrDefault(0, -1);
        if (playerOpponent > 0) {
            this.playerBattle(playerOpponent, true);
            fightRequests.remove(0);
        }
        for (var entry : fightRequests.entrySet()) {
            if (entry.getValue() == 0) {
                this.playerBattle(entry.getKey(), false);
                continue;
            }
            Battle battle = new Battle(this.entityManager.getEntity(entry.getKey()),
                    this.entityManager.getEntity(entry.getValue()));
            battle.fight();
        }
    }

    private void playerBattle(int opponentID, boolean startedByPlayer) {
        //TODO this
    }

    private void playerPlay() throws IOException {
        this.continuePlay = true;
        /*
        WORKFLOW:
        1) Show main screen with options
        2) Read input
        3) Perform the selected action
        4) GOTO 1)
        * */
        while (this.continuePlay) {
            this.output.showMainScreen();
            Input.askOptions(
                    new Input.Option("Quit game", this::quitGame),
                    new Input.Option("Buy/sell goods", this::buySellGoods),
                    new Input.Option("Manage ship", this::manageShip),
                    new Input.Option("Seek markets", this::seekMarkets),
                    new Input.Option("Travel to another planet", this::travelToPlanet));
        }
    }

    private void buySellGoods() throws IOException {
        Player player = this.entityManager.getPlayer();
        var currPlanetGoods = this.entityManager.getPlayer().getCurrPlanet().getGoodsPrices();
        var playerGoods = player.entityStats;

        while (true) {
            int freeSpace = player.getMaxCargo() - player.getCurrCargo();
            this.output.showBuySellScreen();
            var choice = Input.askNumber(0, 9);
            if (choice == 0) {
                return;
            }
            this.output.show("How much of %s would you like to buy or sell? ",
                    GoodsIndex.values()[choice].toString());
            while (true) {
                var amount = Input.askNumber(-playerGoods.getGoodAmount(choice), currPlanetGoods.getGoodAmount(choice));
                int price = currPlanetGoods.getPrice(choice) * amount;
                if (amount > 0) { // Buying
                    if (price > playerGoods.getCredits()) {
                        this.output.show("\nYou don't have enough credits!\n");
                        continue;
                    }
                    if (amount > freeSpace) {
                        this.output.show("\nYou don't have enough space on the ship!\n");
                        continue;
                    }
                    playerGoods.addGood(choice, amount);
                    playerGoods.removeCredits(price);
                    currPlanetGoods.removeGood(choice, amount);
                } else if (amount == 0) { // Cancel
                    break;
                } else { // Selling
                    playerGoods.removeGood(choice, -amount);
                    playerGoods.addCredits(-price);
                    currPlanetGoods.addGood(choice, -amount);
                }
            }
        }
    }
    private void quitGame() {
        this.continuePlay = false;
        this.entityManager.getPlayer().kill();
    }
    private void manageShip() {

    }
    private void seekMarkets() {

    }
    private void travelToPlanet(){

    }

    private final Output output = new Output();
    private class Output {
        private final AnsiPrintStream console = AnsiConsole.out();
        private void clearScreen() {
            console.print(Ansi.ansi().eraseScreen());
        }
        private void showMainScreen() {
            Player player = Game.this.entityManager.getPlayer();

            this.clearScreen();
            this.show("Current planet: %s (%s)\n", player.getCurrPlanet().getName(),
                    player.getCurrPlanet().getPlanetType());
            this.show("Credits: %d\n", player.getCredits());
            this.show("Hull:  %d/%d\n", player.getCurrHull(), player.getMaxHull());
            this.show("Fuel:  %d/%d\n", player.getCurrFuel(), player.getMaxFuel());
            this.show("Cargo: %d/%d\n", player.getCurrCargo(), player.getMaxCargo());
        }

        private void showBuySellScreen() {
            EntityStats stats = Game.this.entityManager.getPlayer().entityStats;
            GoodsPrices goodsPrices = Game.this.entityManager.getPlayer().getCurrPlanet().getGoodsPrices();

            this.clearScreen();
            this.show("#  Type        Cargo Planet Price\n");
            for (var index : GoodsIndex.values()) {
                this.show("%d) %s%s%d%s%d%s%d\n",
                        index.ordinal() + 1,
                        index.toString(),
                        " ".repeat(12 - index.toString().length()),
                        stats.getGoodAmount(index),
                        " ".repeat(6 - getDigits(stats.getGoodAmount(index))),
                        goodsPrices.getGoodAmount(index),
                        " ".repeat(7 - getDigits(goodsPrices.getGoodAmount(index))),
                        goodsPrices.getPrice(index)
                        );
            }
            this.show("Credits: %d\n\n", stats.getCredits());
            this.show("Enter good number to buy or sell it\n0) To go back\n");
        }
        private void showShipInfo() {
            EntityStats stats = Game.this.entityManager.getPlayer().entityStats;
            this.show("Your ship's statistics:\n");
        }
        private void show(String format, Object... objects) {
            console.printf(format, objects);
        }

        private int getDigits(int num) {
            if (num == 0) {
                return 1;
            }
            if (num < 0) {
                num *= -1;
            }
            return (int) (Math.log10(num) + 1);
        }
    }
    private static class Input {
        private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        public record Option(String msg, RunnableWException action) {}
        private static void askOptions(Option... options) throws IOException {
            for (int i = 0; i < options.length; ++i) {
                System.out.printf("%d) %s\n", i, options[i].msg());
            }
            int optionNumber = askNumber(0, options.length);
            options[optionNumber].action.run();
        }

        private static int askNumber(int minimum, int maximum) throws IOException {
            while (true) {
                String line = bufferedReader.readLine();
                try {
                    int optionNumber = Integer.parseInt(line);
                    if (optionNumber < minimum || optionNumber > maximum) {
                        throw new Exception();
                    }
                    return optionNumber;
                } catch (Exception ignored) {
                    System.out.println("Invalid option: " + line);
                }
            }
        }

        private static String askString(Predicate<String> predicate) throws IOException {
            String input;
            while (!predicate.test(input = bufferedReader.readLine())) {}

            return input;
        }

        private record Transaction(int good, int amount) {}
    }
}
