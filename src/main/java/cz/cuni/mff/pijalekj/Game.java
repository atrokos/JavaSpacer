package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.battle.Battle;
import cz.cuni.mff.pijalekj.builders.ShipBuilder;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.entities.*;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.ships.Ship;
import cz.cuni.mff.pijalekj.utils.RunnableWException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Game  {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();
    private boolean firstTime = true;

    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        this.locationsManager = lm;
        this.criminalsManager = cm;
        this.entityManager = em;
    }

    public boolean play() throws Exception {
        if (this.clock.tick()) {
            this.entityManager.resetNPCs(this.criminalsManager, this.locationsManager);
            this.locationsManager.updateAllPlanets();
        }
//        if (firstTime) {
//            this.setPlayerName();
//            firstTime = false;
//        }
//        this.playerPlay();
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
            if (!(entityManager.getEntity(entry.getKey()) instanceof Police)) {
                criminalsManager.addCriminal(entry.getKey());
            }
            Battle battle = new Battle(this.entityManager.getEntity(entry.getKey()),
                    this.entityManager.getEntity(entry.getValue()));
            battle.fight();
        }
    }

    private void playerBattle(int opponentID, boolean startedByPlayer) {
        // TODO This + ask player if they want to fight someone present when traveling
    }

    private void playerPlay() throws Exception {
        Player player = entityManager.getPlayer();
        if (player.isTraveling()) {
            player.travel();
        }
        while (!player.isTraveling()) {
            try {
                this.output.showMainScreen();
                Input.askOptions(
                        new Input.Option("Quit game", this::quitGame),
                        new Input.Option("Buy/sell goods", this::buySellGoods),
                        new Input.Option("Manage ship", this::manageShip),
                        new Input.Option("Seek markets", this::seekMarkets),
                        new Input.Option("Travel to another planet", this::travelToPlanet));
            } catch (Exception e) {
                break;
            }
        }
    }

    private void scanNeighbors(Planet planet) throws IOException {
        var neighbors = locationsManager.getNeighborsOf(planet.getPlanetID());

        while (true) {
            this.output.clearScreen();
            this.output.show("Currently scanned planet: %s\n", planet.getName());
            this.output.goodsAnalysis(planet);
            this.output.showPlanetNeighbors(planet.getPlanetID(), "Where to seek next?\n");
            int input = Input.askNumber(0, neighbors.length);
            if (input == 0) {
                return;
            }
            scanNeighbors(locationsManager.getPlanet(neighbors[input-1]));
        }
    }

    private void setPlayerName() throws IOException {
        this.output.clearScreen();
        this.output.askPlayerName();
        String name = Input.askString(String::isBlank, this.output.console);
        if (name == null) {
            throw new IOException("Player name is null; terminating.");
        }
        this.entityManager.setPlayerName(name);
    }
    private void buySellGoods() throws IOException {
        Player player = this.entityManager.getPlayer();
        var currPlanetGoods = this.entityManager.getPlayer().getCurrPlanet().getGoodsPrices();
        var playerGoods = player.entityStats;

        while (true) {
            int freeSpace = player.getMaxCargo() - player.getCurrCargo();
            this.output.showBuySellScreen();
            var choice = Input.askNumber(0, GoodsIndex.values().length);
            if (choice == 0) {
                return;
            }
            choice -= 1; // Chosen number is always +1 higher than the actual index in the array (0 is for going back)
            this.output.show("How many %s would you like to buy or sell?\n",
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
                    break;
                } else if (amount == 0) { // Cancel
                    break;
                } else { // Selling
                    playerGoods.removeGood(choice, -amount);
                    playerGoods.addCredits(-price);
                    currPlanetGoods.addGood(choice, -amount);
                    break;
                }
            }
        }
    }
    private void quitGame() throws Exception {
        this.entityManager.getPlayer().kill();
        throw new Exception();
    }
    private void manageShip() {
        Player player = this.entityManager.getPlayer();
        while (true) {
            try {
                this.output.showShipInfo();

                Input.askOptions(
                        new Input.Option("Go back", this::goBack),
                        new Input.Option("Repair hull", this::repairHull),
                        new Input.Option("Refuel ship", this::refuelShip),
                        new Input.Option("Buy a new ship", this::buyNewShip)
                );
            } catch (Exception ignored) {
                break;
            }
        }
    }
    public void repairHull() throws IOException {
        Player player = this.entityManager.getPlayer();
        int hullDiff = player.getMaxHull() - player.getCurrHull();
        int hullCost = hullDiff * Constants.repairCost;
        int newHullValue = player.getMaxHull();

        this.output.clearScreen();
        if (hullCost > player.getCredits()) {
            int maxHullPossible = (player.getCredits() / Constants.repairCost);
            int newCost = maxHullPossible * Constants.repairCost;
            newHullValue = player.getCurrHull() + maxHullPossible;

            this.output.show("Fully repairing the hull would cost %d, which you can't afford.\n", hullCost);
            this.output.show("You can repair your ship to %d, which would cost %d. You have %d credits.\n",
                    newHullValue, newCost, player.getCredits());

            hullCost = newCost;
        } else {
            this.output.show("Repairing the ship to full costs %d credits (you have %d).\n",
                    hullCost, player.getCredits());
        }

        this.output.show("0) Cancel repairs\n1) Repair hull");
        int input = Input.askNumber(0, 1);
        if (input != 0) {
            player.ownedShip.repairHull(newHullValue);
            player.entityStats.removeCredits(hullCost);
        }
    }
    public void refuelShip() throws IOException {
        Player player = this.entityManager.getPlayer();
        int fuelDiff = player.getMaxFuel() - player.getCurrFuel();
        int fuelCost = fuelDiff * Constants.fuelCost;
        int newFuelValue = player.getMaxFuel();

        this.output.clearScreen();
        if (fuelCost > player.getCredits()) {
            int maxFuelPossible = (player.getCredits() / Constants.fuelCost);
            int newCost = maxFuelPossible * Constants.fuelCost;
            newFuelValue = player.getCurrFuel() + maxFuelPossible;
            this.output.show("Fully refueling your ship would cost %d, which you can't afford.\n", fuelCost);
            this.output.show("You can refuel your ship to %d, which would cost %d. You have %d credits.\n",
                    newFuelValue, newCost, player.getCredits());
            this.output.show("0) Cancel repairs\n1) Repair hull");

            fuelCost = newCost;
        } else {
            this.output.show("Fully refueling your ship costs %d credits (you have %d).\n",
                    fuelCost, player.getCredits());
        }

        this.output.show("0) Go back\n1) Refuel ship");
        int input = Input.askNumber(0, 1);
        if (input != 0) {
            player.ownedShip.repairHull(newFuelValue);
            player.entityStats.removeCredits(fuelCost);
        }
    }

    public void buyNewShip() throws IOException {
        Player player = entityManager.getPlayer();
        this.output.showShipDealership();
        if (player.getCurrCargo() > 0) {
            this.output.show("\nYou have some goods in your cargo. Sell them so that you can buy the new ship!\n");
//            this.output.showShipDealership();
            int choice = Input.askNumber(0, 0);
            if (choice == 0) {
                return;
            }
        }

        while (true) {
            int choice = Input.askNumber(0, ShipType.values().length);
            if (choice == 0) {
                return;
            }
            ShipType chosenShip = ShipType.values()[choice - 1];
            int currShipPrice = Constants.builders.getLong
                    ("Ships.Prices." + player.ownedShip.getShipType()).intValue();
            int shipPrice = Constants.builders.getLong("Ships.Prices." + chosenShip).intValue();
            if (shipPrice > player.getCredits()) {
                this.output.show("\nSorry, you cannot afford this ship!\n");
            } else {
                player.ownedShip = ShipBuilder.buildShip(chosenShip);
                player.entityStats.removeCredits(shipPrice);
                player.entityStats.addCredits(currShipPrice);
                break;
            }
        }
    }

    public void goBack() throws Exception {
        throw new Exception();
    }
    private void seekMarkets() throws IOException {
        scanNeighbors(entityManager.getPlayer().getCurrPlanet());
    }
    private void travelToPlanet() throws Exception {
        Player player = entityManager.getPlayer();
        int currPlanetID = player.getCurrPlanet().getPlanetID();
        var neighbors = locationsManager.getNeighborsOf(currPlanetID);

        this.output.showTravelOptions(currPlanetID);
        while (true) {
            int choice = Input.askNumber(0, neighbors.length);
            if (choice == 0) {
                return;
            }
            choice -= 1; // Chosen number is always +1 higher than the actual index in the array (0 is for going back)
            if (locationsManager.getDistanceBetween(currPlanetID, neighbors[choice]) > player.getCurrFuel()) {
                this.output.show("You don't have enough fuel for this trip!\n");
            } else {
                Game.this.entityManager.getPlayer().travelTo(neighbors[choice]);
                throw new Exception();
            }
        }
    }
    private final Output output = new Output();
    private class Output {
        private final PrintStream console = System.out;
        private void clearScreen() {
            console.print("\033c");
        }

        private void goodsAnalysis(Planet otherPlanet) {
            var currPlanetGoods = entityManager.getPlayer().getCurrPlanet().getGoodsPrices();
            var otherPlanetGoods = otherPlanet.getGoodsPrices();

            this.show("Name         Amount Price\n");
            for (var index : GoodsIndex.values()){
                int priceOther = otherPlanetGoods.getPrice(index);
                int priceDiff = priceOther - currPlanetGoods.getPrice(index);
                int goodAmount = otherPlanetGoods.getGoodAmount(index);

                this.show("%s%s%d%s%d (%d)\n",
                        index,
                        " ".repeat(13 - index.toString().length()),
                        goodAmount,
                        " ".repeat(7 - getDigits(goodAmount)),
                        priceOther,
                        priceDiff);
            }
        }
        private void showTravelOptions(int currPlanetID) {
            this.clearScreen();
            this.show("Fuel: %d\n", entityManager.getPlayer().getCurrFuel());
            this.showPlanetNeighbors(currPlanetID, "Where would you like to travel?\n");
        }

        private void showPlanetNeighbors(int planetID, String msg) {
            var neighbors = locationsManager.getNeighborsOf(planetID);
            List<String> options = new ArrayList<>();
            options.add("Go back");
            for (var neighbor : neighbors) {
                options.add("%s (%d jumps)".formatted(
                        locationsManager.getPlanetName(neighbor),
                        locationsManager.getDistanceBetween(planetID, neighbor)));
            }
            this.showSimpleOptions(options);
            this.show("\n" + msg);
        }
        private void showShipDealership() {
            this.clearScreen();
            this.show("Welcome to our shop! We have these ships ready for you to buy:\n");
            this.show("# Ship       Price\n");
            for (var shipType : ShipType.values()) {
                this.show("%d) %s%s%d\n",
                        shipType.ordinal() + 1,
                        shipType,
                        " ".repeat(10 - shipType.toString().length()),
                        Constants.builders.getLong("Ships.Prices." + shipType));
            }
            this.show("Credits: %d\n\n0) Go back\n", entityManager.getPlayer().getCredits());
        }
        private void showMainScreen() {
            Player player = entityManager.getPlayer();

            this.clearScreen();
            this.show("Current planet: %s (%s)\n", player.getCurrPlanet().getName(),
                    player.getCurrPlanet().getPlanetType());
            this.show("Credits: %d\n", player.getCredits());
            this.show("Hull:    %d/%d\n", player.getCurrHull(), player.getMaxHull());
            this.show("Fuel:    %d/%d\n", player.getCurrFuel(), player.getMaxFuel());
            this.show("Cargo:   %d/%d\n\n", player.getCurrCargo(), player.getMaxCargo());
        }

        private void showSimpleOptions(List<String> options) {
            for (int i = 0; i < options.size(); ++i) {
                this.show("%d) %s\n", i, options.get(i));
            }
        }

        private void showBuySellScreen() {
            Player player = Game.this.entityManager.getPlayer();
            EntityStats stats = player.entityStats;
            GoodsPrices goodsPrices = player.getCurrPlanet().getGoodsPrices();

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
            this.show("Credits: %d\n", stats.getCredits());
            this.show("Cargo:   %d/%d\n\n", player.getCurrCargo(), player.getMaxCargo());
            this.show("0) Go back\nEnter good number to buy or sell it\n");
        }
        private void showShipInfo() {
            Player player = Game.this.entityManager.getPlayer();
            Ship ship = player.ownedShip;

            this.clearScreen();
            this.show("Your ship's statistics:\n");
            this.show("Hull:    %s\n", ship.getStats().hull.toString());
            this.show("Shields: %s\n", ship.getStats().shields.toString());
            this.show("Cargo:   %d/%d\n", player.getCurrCargo(), player.getMaxCargo());
            this.show("Fuel:    %s\n", ship.getStats().fuel.toString());
            this.show("Type:    %s\n\n", ship.getShipType().toString());
        }
        private void askPlayerName() {
            this.show("Welcome to Space Trader! Enter your name:\n");
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
        private static void askOptions(Option... options) throws Exception {
            for (int i = 0; i < options.length; ++i) {
                System.out.printf("%d) %s\n", i, options[i].msg());
            }
            int optionNumber = askNumber(0, options.length);
            options[optionNumber].action.run();
        }

        private static int askNumber(int minimum, int maximum) throws IOException {
            while (true) {
                System.out.print(">>> ");
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

        private static String askString(Predicate<String> predicate, PrintStream stream) throws IOException {
            String input;
            while (predicate.test(input = bufferedReader.readLine())) {
                stream.println("Name cannot be blank!");
            }

            return input;
        }

        private record Transaction(int good, int amount) {}
    }
}
