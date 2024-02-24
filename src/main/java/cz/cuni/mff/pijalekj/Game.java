package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.battle.Battle;
import cz.cuni.mff.pijalekj.battle.Playerlike;
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
import java.util.*;
import java.util.function.Predicate;

/**
 * Represents the actual game. It is self-contained; there are no other dependencies other than
 * that all member variables have to be initialized.
 */
public class Game  {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();
    /** Whether the game cycle runs for the first time. */
    private boolean firstTime = true;
    /** Whether it should offer attacking ships when traveling. */
    private boolean searchOpponents = true;

    /**
     * Constructs the game. Note that the player's name has to be set after this, using the setPlayerName() method.
     * @param lm The LocationsManager the game is associated with.
     * @param cm The CriminalsManager the game is associated with.
     * @param em The EntityManager the game is associated with.
     */
    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        locationsManager = lm;
        criminalsManager = cm;
        entityManager = em;
    }

    /**
     * Main gameplay loop.
     * @return Whether to run another game cycle.
     * @throws Exception Any exception that is handled outside.
     */
    public boolean play() throws Exception {
        handleGameTick();

        if (firstTime) {
            setPlayerName();
            firstTime = false;
        }

        OptionalInt playerActionResult = playerPlay();
        HashMap<Integer, Integer> fightRequests = entityManager.play(playerActionResult);
        handleBattles(fightRequests);
        criminalsManager.updateCriminals();

        return entityManager.getPlayer().isAlive();
    }

    /**
     * Updates all planets and resets all NPCs during the Big tick.
     */
    private void handleGameTick() {
        if (clock.tick()) {
            entityManager.resetNPCs(criminalsManager, locationsManager);
            locationsManager.updateAllPlanets();
        }
    }

    /**
     * Handles all requested battles in the fightRequests map.
     * @param fightRequests All fight requests in the form Attacker -> Attacked.
     */
    private void handleBattles(HashMap<Integer, Integer> fightRequests) throws IOException {
        handlePlayerBattle(fightRequests);
        handleOtherBattles(fightRequests);
    }

    /**
     * Manage Player's battle, if any.
     */
    private void handlePlayerBattle(HashMap<Integer, Integer> fightRequests) throws IOException {
        int playerOpponent = fightRequests.getOrDefault(-1, -2);
        if (playerOpponent >= 0) {
            criminalsManager.addCriminal(-1);
            playerBattle(playerOpponent, true);
            fightRequests.remove(-1);
        }
    }

    /** Handle battles of all NPCs */
    private void handleOtherBattles(HashMap<Integer, Integer> fightRequests) throws IOException {
        for (var entry : fightRequests.entrySet()) {
            if (entry.getValue() == -1) {
                playerBattle(entry.getKey(), false);
                continue;
            }
            Entity attacker = entityManager.getEntity(entry.getKey());
            Entity attacked = entityManager.getEntity(entry.getValue());
            if (!(attacker instanceof Police)) {
                criminalsManager.addCriminal(entry.getKey());
            }
            if (attacker.isAlive() && attacked.isAlive()){
                Battle battle = new Battle(attacker, attacked);
                battle.fight();
            } else {
                criminalsManager.removeCriminal(entry.getKey());
                criminalsManager.removeCriminal(entry.getValue());
            }
        }
    }

    /**
     * Manages all logic and dialogues of the player.
     * @param opponentID The internal EntityID of the opponent.
     * @param startedByPlayer Whether the attack was started by the player or the opponent.
     * @throws IOException When reading the input.
     */
    private void playerBattle(int opponentID, boolean startedByPlayer) throws IOException {
        Entity opponent = entityManager.getEntity(opponentID);
        Ship playersShip = entityManager.getEntityShip(-1);
        Random generator = new Random();
        int playersDamage = Ship.damageOutput(playersShip, opponent.getOwnedShip());
        output.clearScreen();
        if (!startedByPlayer) {
            output.show("You are under attack!\n");
        }
        while (playersShip.isAlive() && opponent.isAlive()) {
            output.showBattleInfo(opponent);
            output.showSimpleOptions("Attack", "Flee");
            int input = Input.askNumber(0, 1);
            if (input == 0) {
                opponent.takeDamage(playersDamage);
            } else {
                double dieThrow = generator.nextInt(0, 101);
                if (dieThrow <= playersShip.getFleeChance()) {
                    return; // One party fled
                }
            }

            var attackerAction = opponent.battle(entityManager.getPlayer());
            switch (attackerAction.actionType()) {
                case attack -> playersShip.takeDamage(attackerAction.value());
                case flee -> {
                    double dieThrow = generator.nextInt(0, 101);
                    if (dieThrow <= attackerAction.value()) {
                        return; // One party fled
                    }
                }
            }
            output.clearScreen();
        }
        if (!playersShip.isAlive())
            output.show("You died!");
    }

    /** Manages the logic when it's the player's turn. */
    private OptionalInt playerPlay() throws Exception {
        Player player = entityManager.getPlayer();
        if (player.isTraveling()) {
            if (searchOpponents) {
                searchOpponents = false;
                return searchForOpponents();
            }
            player.travel();
        }

        while (!player.isTraveling() && player.isAlive()) {
            player.ownedShip.rechargeShields();
            searchOpponents = true;
            output.showMainScreen();
            handlePlayerOptions();
        }

        return OptionalInt.empty();
    }
    /** Handle all player's options when in the main menu. */
    private void handlePlayerOptions() throws Exception {
        Input.askOptions(
                new Input.Option("Quit game", this::quitGame),
                new Input.Option("Buy/sell goods", this::buySellGoods),
                new Input.Option("Manage ship", this::manageShip),
                new Input.Option("Seek markets", this::seekMarkets),
                new Input.Option("Travel to another planet", this::travelToPlanet)
        );
    }
    /** Searches for possible opponents that are on the same path as the player. Asks the player if they want to
     * attack the found NPC. */
    private OptionalInt searchForOpponents() throws IOException {
        Player player = entityManager.getPlayer();
        var presentEntities = player.getPresentEntities();
        var possibleVictimID = presentEntities.stream().filter(ID -> ID != -1).findFirst();
        if (possibleVictimID.isEmpty()) {
            return OptionalInt.empty();
        }
        output.clearScreen();
        Ship opponentShip = entityManager.getEntityShip(possibleVictimID.get());
        output.show("While traveling, you meet a ship:\n");
        output.showShipBattle(opponentShip, player.getOwnedShip());

        output.show("\nWhat would you like to do?\n");
        output.showSimpleOptions("Ignore it", "Attack it");
        int input = Input.askNumber(0, 1);

        if (input == 0) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(possibleVictimID.get());
    }
    /** Shows details about the given Planet. */
    private void scanNeighbors(Planet planet) throws IOException {
        var neighbors = locationsManager.getNeighborsOf(planet.planetID());

        while (true) {
            output.clearScreen();
            output.show("Currently scanned planet: %s\n", planet.name());
            output.goodsAnalysis(planet);
            output.showPlanetNeighbors(planet.planetID(), "Where to seek next?\n");
            int input = Input.askNumber(0, neighbors.length);
            if (input == 0) {
                return;
            }
            scanNeighbors(locationsManager.getPlanet(neighbors[input-1]));
        }
    }
    /** Asks user for the player's name and sets it. */
    private void setPlayerName() throws IOException {
        output.clearScreen();
        output.askPlayerName();
        String name = Input.askString(String::isBlank, output.console);
        if (name == null) {
            throw new IOException("Player name is null; terminating.");
        }
        entityManager.setPlayerName(name);
    }
    /** Manages logic and dialogues for the BuySell screen. */
    private void buySellGoods() throws IOException {
        Player player = entityManager.getPlayer();
        var currPlanetGoods = entityManager.getPlayer().getCurrPlanet().goodsPrices();
        var playerGoods = player.entityStats;

        while (true) {
            int freeSpace = player.getMaxCargo() - player.getCurrCargo();
            output.showBuySellScreen();
            var choice = Input.askNumber(0, GoodsIndex.values().length);
            if (choice == 0) {
                return;
            }
            choice -= 1; // Chosen number is always +1 higher than the actual index in the array (0 is for going back)
            output.show("How many %s would you like to buy or sell?\n",
                    GoodsIndex.values()[choice].toString());
            while (true) {
                var amount = Input.askNumber(-playerGoods.getGoodAmount(choice), currPlanetGoods.getGoodAmount(choice));
                int price = currPlanetGoods.getPrice(choice) * amount;
                if (amount > 0) { // Buying
                    if (price > playerGoods.getCredits()) {
                        output.show("\nYou don't have enough credits!\n");
                        continue;
                    }
                    if (amount > freeSpace) {
                        output.show("\nYou don't have enough space on the ship!\n");
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
    /** Kills the player, effectively ending the game. */
    private void quitGame() {
        entityManager.getPlayer().kill();
    }
    /** Gives the player all options that manage their ship. */
    private void manageShip() {
        while (true) {
            try {
                output.showShipInfo();

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
    /** Shows the dialogue for repairing the player's ship. */
    public void repairHull() throws IOException {
        Player player = entityManager.getPlayer();
        int hullDiff = player.getMaxHull() - player.getCurrHull();
        int hullCost = hullDiff * Constants.repairCost;
        int newHullValue = player.getMaxHull();

        output.clearScreen();
        if (hullCost > player.getCredits()) {
            int maxHullPossible = (player.getCredits() / Constants.repairCost);
            int newCost = maxHullPossible * Constants.repairCost;
            newHullValue = player.getCurrHull() + maxHullPossible;

            output.show("Fully repairing the hull would cost %d, which you can't afford.\n", hullCost);
            output.show("You can repair your ship to %d, which would cost %d. You have %d credits.\n",
                    newHullValue, newCost, player.getCredits());

            hullCost = newCost;
        } else {
            output.show("Repairing the ship to full costs %d credits (you have %d).\n",
                    hullCost, player.getCredits());
        }

        output.show("0) Cancel repairs\n1) Repair hull\n\n");
        int input = Input.askNumber(0, 1);
        if (input != 0) {
            player.ownedShip.repairHull(newHullValue);
            player.entityStats.removeCredits(hullCost);
        }
    }
    /** Shows the dialogue for repairing the player's ship. */
    public void refuelShip() throws IOException {
        Player player = entityManager.getPlayer();
        int fuelDiff = player.getMaxFuel() - player.getCurrFuel();
        int fuelCost = fuelDiff * Constants.fuelCost;
        int newFuelValue = player.getMaxFuel();

        output.clearScreen();
        if (fuelCost > player.getCredits()) {
            int maxFuelPossible = (player.getCredits() / Constants.fuelCost);
            int newCost = maxFuelPossible * Constants.fuelCost;
            newFuelValue = player.getCurrFuel() + maxFuelPossible;
            output.show("Fully refueling your ship would cost %d, which you can't afford.\n", fuelCost);
            output.show("You can refuel your ship to %d, which would cost %d. You have %d credits.\n",
                    newFuelValue, newCost, player.getCredits());
            output.show("0) Cancel repairs\n1) Repair hull");

            fuelCost = newCost;
        } else {
            output.show("Fully refueling your ship costs %d credits (you have %d).\n",
                    fuelCost, player.getCredits());
        }

        output.show("0) Go back\n1) Refuel ship\n\n");
        int input = Input.askNumber(0, 1);
        if (input != 0) {
            player.ownedShip.refuel(newFuelValue);
            player.entityStats.removeCredits(fuelCost);
        }
    }
    /** Manages the dialogue for buying a new ship. */
    public void buyNewShip() throws IOException {
        Player player = entityManager.getPlayer();
        output.showShipDealership();
        if (player.getCurrCargo() > 0) {
            output.show("\nYou have some goods in your cargo. Sell them so that you can buy the new ship!\n");
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
                output.show("\nSorry, you cannot afford this ship!\n");
            } else {
                player.ownedShip = ShipBuilder.buildShip(chosenShip);
                player.entityStats.removeCredits(shipPrice);
                player.entityStats.addCredits(currShipPrice);
                break;
            }
        }
    }
    /** Throws an exception, used for fallback to a try-catch block.  */
    public void goBack() throws IOException {
        throw new IOException();
    }
    /** Start case for the recursive scanNeighbors. */
    private void seekMarkets() throws IOException {
        scanNeighbors(entityManager.getPlayer().getCurrPlanet());
    }
    /** Manages the dialogue for traveling to another planet. */
    private void travelToPlanet() throws IOException {
        Player player = entityManager.getPlayer();
        int currPlanetID = player.getCurrPlanet().planetID();
        var neighbors = locationsManager.getNeighborsOf(currPlanetID);

        output.showTravelOptions(currPlanetID);
        while (true) {
            int choice = Input.askNumber(0, neighbors.length);
            if (choice == 0) {
                return;
            }
            choice -= 1; // Chosen number is always +1 higher than the actual index in the array (0 is for going back)
            if (locationsManager.getDistanceBetween(currPlanetID, neighbors[choice]) > player.getCurrFuel()) {
                output.show("You don't have enough fuel for this trip!\n");
            } else {
                entityManager.getPlayer().travelTo(neighbors[choice]);
                return;
            }
        }
    }
    private final Output output = new Output();
    private class Output {
        private final PrintStream console = System.out;

        /**
         * Resets the terminal, clearing it.
         */
        private void clearScreen() {
            console.print("\033c");
        }

        /**
         * Displays battle information between the player and an opponent.
         *
         * @param opponent The opponent to display battle information about.
         */
        private void showBattleInfo(Playerlike opponent) {
            show("===== Battle info =====\n");
            show("Your stats\n");
            showShipBattle(entityManager.getEntityShip(-1), opponent.getOwnedShip());
            show("\nOpponent's stats\n");
            showShipBattle(opponent.getOwnedShip(), entityManager.getEntityShip(-1));
        }

        /**
         * Displays the battle statistics of a ship.
         *
         * @param displayedShip The ship whose stats are to be displayed.
         * @param ship2         The second ship for comparison.
         */
        private void showShipBattle(Ship displayedShip, Ship ship2) {
            show("Hull:    %s\n", displayedShip.getStats().hull.toString());
            show("Shields: %s\n", displayedShip.getStats().shields.toString());
            show("Damage:  %d\n\n", Ship.damageOutput(displayedShip, ship2));
        }

        /**
         * Displays the analysis of goods prices between the current planet and another planet.
         *
         * @param otherPlanet The planet to compare goods prices with.
         */
        private void goodsAnalysis(Planet otherPlanet) {
            var currPlanetGoods = entityManager.getPlayer().getCurrPlanet().goodsPrices();
            var otherPlanetGoods = otherPlanet.goodsPrices();

            show("Name         Amount Price\n");
            for (var index : GoodsIndex.values()) {
                int priceOther = otherPlanetGoods.getPrice(index);
                int priceDiff = priceOther - currPlanetGoods.getPrice(index);
                int goodAmount = otherPlanetGoods.getGoodAmount(index);

                show("%s%s%d%s%d (%d)\n",
                        index,
                        " ".repeat(13 - index.toString().length()),
                        goodAmount,
                        " ".repeat(7 - getDigits(goodAmount)),
                        priceOther,
                        priceDiff);
            }
        }

        /**
         * Displays the available travel options from the current planet.
         *
         * @param currPlanetID The ID of the current planet.
         */
        private void showTravelOptions(int currPlanetID) {
            clearScreen();
            show("Fuel: %d\n", entityManager.getPlayer().getCurrFuel());
            showPlanetNeighbors(currPlanetID, "Where would you like to travel?\n");
        }

        /**
         * Displays the neighbors of a planet along with travel options.
         *
         * @param planetID The ID of the current planet.
         * @param msg      The message to display.
         */
        private void showPlanetNeighbors(int planetID, String msg) {
            var neighbors = locationsManager.getNeighborsOf(planetID);
            List<String> options = new ArrayList<>();
            options.add("Go back");
            for (var neighbor : neighbors) {
                options.add("%s (%d jumps)".formatted(
                        locationsManager.getPlanetName(neighbor),
                        locationsManager.getDistanceBetween(planetID, neighbor)));
            }
            showSimpleOptions(options.toArray(new String[0]));
            show("\n" + msg);
        }

        /**
         * Displays the available ships for purchase in a dealership.
         */
        private void showShipDealership() {
            clearScreen();
            show("Welcome to our shop! We have these ships ready for you to buy:\n");
            show("# Ship       Price\n");
            for (var shipType : ShipType.values()) {
                show("%d) %s%s%d\n",
                        shipType.ordinal() + 1,
                        shipType,
                        " ".repeat(10 - shipType.toString().length()),
                        Constants.builders.getLong("Ships.Prices." + shipType));
            }
            show("Credits: %d\n\n0) Go back\n", entityManager.getPlayer().getCredits());
        }

        /**
         * Displays the main screen with player information.
         */
        private void showMainScreen() {
            Player player = entityManager.getPlayer();

            clearScreen();
            show("Current planet: %s (%s)\n", player.getCurrPlanet().name(),
                    player.getCurrPlanet().planetType());
            show("Credits: %d\n", player.getCredits());
            show("Hull:    %d/%d\n", player.getCurrHull(), player.getMaxHull());
            show("Fuel:    %d/%d\n", player.getCurrFuel(), player.getMaxFuel());
            show("Cargo:   %d/%d\n\n", player.getCurrCargo(), player.getMaxCargo());
        }

        /**
         * Displays a list of simple options with corresponding indices.
         *
         * @param options The list of options to display.
         */
        private void showSimpleOptions(String... options) {
            for (int i = 0; i < options.length; ++i) {
                show("%d) %s\n", i, options[i]);
            }
        }

        /**
         * Displays the buy/sell screen for goods.
         */
        private void showBuySellScreen() {
            Player player = entityManager.getPlayer();
            EntityStats stats = player.entityStats;
            GoodsPrices goodsPrices = player.getCurrPlanet().goodsPrices();

            clearScreen();
            show("#  Type        Cargo Planet Price\n");
            for (var index : GoodsIndex.values()) {
                show("%d) %s%s%d%s%d%s%d\n",
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
            show("Credits: %d\n", stats.getCredits());
            show("Cargo:   %d/%d\n\n", player.getCurrCargo(), player.getMaxCargo());
            show("0) Go back\nEnter good number to buy or sell it\n");
        }

        /**
         * Displays information about the player's owned ship.
         */
        private void showShipInfo() {
            Player player = entityManager.getPlayer();
            Ship ship = player.ownedShip;

            clearScreen();
            show("Your ship's statistics:\n");
            show("Hull:    %s\n", ship.getStats().hull.toString());
            show("Shields: %s\n", ship.getStats().shields.toString());
            show("Cargo:   %d/%d\n", player.getCurrCargo(), player.getMaxCargo());
            show("Fuel:    %s\n", ship.getStats().fuel.toString());
            show("Type:    %s\n\n", ship.getShipType().toString());
        }

        /**
         * Prompts the player to enter their name.
         */
        private void askPlayerName() {
            show("Welcome to Space Trader! Enter your name:\n");
        }

        /**
         * Displays a formatted string using the console.
         *
         * @param format  The format string.
         * @param objects The objects to format.
         */
        private void show(String format, Object... objects) {
            console.printf(format, objects);
        }

        /**
         * Returns the number of digits in a given integer.
         *
         * @param num The integer to count digits for.
         * @return The number of digits in the integer.
         */
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

        /**
         * Represents an option with a message and an associated action.
         */
        public record Option(String msg, RunnableWException action) {}

        /**
         * Prompts the user with a list of options and executes the selected option's action.
         *
         * @param options The list of options to display.
         * @throws Exception If an exception occurs while executing the selected option's action.
         */
        private static void askOptions(Option... options) throws Exception {
            for (int i = 0; i < options.length; ++i) {
                System.out.printf("%d) %s\n", i, options[i].msg());
            }
            int optionNumber = askNumber(0, options.length);
            options[optionNumber].action.run();
        }

        /**
         * Prompts the user to enter a number within a specified range.
         *
         * @param minimum The minimum allowed number.
         * @param maximum The maximum allowed number.
         * @return The selected number.
         * @throws IOException If an I/O error occurs while reading input.
         */
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

        /**
         * Prompts the user to enter a string, applying a predicate to validate the input.
         *
         * @param predicate The predicate to validate the input.
         * @param stream    The PrintStream to display error messages.
         * @return The validated input string.
         * @throws IOException If an I/O error occurs while reading input.
         */
        private static String askString(Predicate<String> predicate, PrintStream stream) throws IOException {
            String input;
            while (predicate.test(input = bufferedReader.readLine())) {
                stream.println("Name cannot be blank!");
            }

            return input;
        }
    }
}
