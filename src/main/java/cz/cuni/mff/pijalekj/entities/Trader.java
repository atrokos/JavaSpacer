package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.battle.Playerlike;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.*;

/**
 * The Trader class represents a trader entity in the game, extending the Entity class.
 * It includes additional functionality for trading and planning travel routes.
 */
public class Trader extends Entity {
    private Deque<Integer> path = new LinkedList<>();

    /**
     * Constructs a Trader object with specified attributes.
     *
     * @param travelManager     The TravelManager for managing travel-related functionalities.
     * @param entityManager     The EntityManager for managing entities in the game.
     * @param criminalsManager  The CriminalsManager for managing criminal entities in the game.
     * @param ownedShip         The Ship owned by the trader.
     * @param entityStats       The EntityStats representing the trader's statistics and inventory.
     * @param prevAction        The previous action of the trader.
     * @param entityID          The ID of the trader entity.
     */
    public Trader(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, ownedShip, entityStats, prevAction, entityID);
    }

    /**
     * Handles the battle decision for the trader. Traders always choose to flee.
     *
     * @param opponent The player or another entity to battle.
     * @return A BattleDecision representing the trader's decision to flee.
     */
    @Override
    public BattleDecision battle(Playerlike opponent) {
        return new BattleDecision(BattleActionType.flee, ownedShip.getFleeChance());
    }

    /**
     * Handles the actions to be performed when the trader wins a battle.
     *
     * @param opponent The player or another entity that the trader defeated.
     */
    @Override
    public void won(Playerlike opponent) {
        takeAll(opponent.getEntityStats());
        entityStats.transferAllCredits(opponent.getEntityStats());
    }

    /**
     * Handles the trader's turn during gameplay, determining actions such as trading and traveling.
     *
     * @return An OptionalInt representing the ID of the entity to interact with (e.g., battle or trade).
     */
    @Override
    public OptionalInt play() {
        if (!isAlive()) {
            return OptionalInt.empty();
        }

        if (travelManager.isTraveling()) {
            travel();
            return OptionalInt.empty();
        }

        maintenance();
        if (isFull()) {
            sell();
        }

        if (path.isEmpty()) {
            if (prevAction != EntityActions.sell) {
                sell();
                prevAction = EntityActions.sell;
                return OptionalInt.empty();
            } else if (ownedShip.getStats().fuel.getCurr() < 13) {
                return OptionalInt.empty();
            } else {
                createPlan();
            }
        } else {
            int nextDestinationID = path.pop();
            travelManager.travelStart(nextDestinationID);
            prevAction = EntityActions.travelPrep;
        }
        return OptionalInt.empty();
    }

    /**
     * Creates a trading plan for the trader, determining the best planets to visit for trading.
     */
    private void createPlan() {
        Deque<Integer> newPath = new LinkedList<>();
        newPath.add(travelManager.getCurrLocationID());

        HashSet<Integer> visited = new HashSet<>();
        var results = findBestPlanet(newPath, 0, Integer.MIN_VALUE, visited, -1);
        var currPlanet = travelManager.getCurrLocation();

        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            entityStats.removeCredits(currPlanet.buy(index, results.toBuy[index]));
            entityStats.addGood(index, results.toBuy[index]);
        }

        results.path.removeFirst();
        path = results.path;
        travelManager.travelStart(path.removeFirst());
    }

    /**
     * Finds the best planet for trading based on a recursive exploration of possible travel routes.
     *
     * @param path          The current path being explored.
     * @param travelLength  The current travel length in the exploration.
     * @param rating        The current rating of the exploration.
     * @param visited       A set of visited planets to avoid revisiting.
     * @param counter       A counter to limit the exploration depth.
     * @return A TraderPlan containing the best path, rating, and items to buy.
     */
    private TraderPlan findBestPlanet(Deque<Integer> path, int travelLength, int rating,
                                      HashSet<Integer> visited, int counter)
    {
        int currPlanetID = path.getLast();
        int bestRating = rating;
        Deque<Integer> bestPath = new LinkedList<>(path);
        int[] toBuy = new int[9];

        ++counter;
        visited.add(currPlanetID);

        if (currPlanetID != travelManager.getCurrLocationID()) {
            var planetRating = ratePlanet(travelManager.getPlanet(currPlanetID), travelLength);
            if (planetRating.rating > bestRating) {
                bestRating = planetRating.rating;
                toBuy = planetRating.toBuy;
            }
        }

        if (counter == 3) {
            return new TraderPlan(bestPath, bestRating, toBuy);
        }

        for (int neighborID : travelManager.getNeighbors(currPlanetID)) {
            if (visited.contains(neighborID)) {
                continue;
            }

            path.addLast(neighborID);
            int distance = travelLength + travelManager.getDistanceBetween(currPlanetID, neighborID);
            var found = findBestPlanet(path, distance, bestRating, visited, counter);
            path.removeLast();

            if (found.rating > bestRating) {
                bestRating = found.rating;
                bestPath = found.path;
                toBuy = found.toBuy;
            }
        }

        return new TraderPlan(bestPath, bestRating, toBuy);
    }

    /**
     * Rates a planet based on its goods prices and distance from the current location.
     *
     * @param planet        The planet to be rated.
     * @param travelLength  The current travel length in the exploration.
     * @return A RatingBuy object containing the rating and items to buy on the planet.
     */
    private RatingBuy ratePlanet(Planet planet, int travelLength) {
        int[] toBuy = new int[9];
        SortedMap<Integer, Integer> goodsDiff = new TreeMap<>();
        var currPlanetGoodsPrices = travelManager.getCurrLocation().goodsPrices();
        var planetGoodsPrices = planet.goodsPrices();
        int rating = -(travelLength * Constants.fuelCost);

        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            int priceDiff = planetGoodsPrices.getPrice(index) - currPlanetGoodsPrices.getPrice(index);

            while (goodsDiff.containsKey(priceDiff)) {
                --priceDiff;
            }
            if (priceDiff > 0)
                goodsDiff.put(priceDiff, index);
        }

        int credits = entityStats.getCredits();
        int freeSpace = ownedShip.getStats().maxCargo - entityStats.getTotalGoodsAmount();
        for (var key : goodsDiff.reversed().keySet()) {
            int index = goodsDiff.get(key);
            int available = currPlanetGoodsPrices.getGoodAmount(index);
            int affordable = credits / currPlanetGoodsPrices.getPrice(index);
            // limit one commodity to 10 pieces max, so that traders won't buy the best one out
            int buyable = Math.min(10, Math.min(available, affordable));

            if (freeSpace - buyable <= 0) {
                toBuy[index] = freeSpace;
                rating += freeSpace * key;
                return new RatingBuy(rating, toBuy);
            }

            toBuy[index] = buyable;
            rating += buyable * key;
            freeSpace -= buyable;
            credits -= buyable * currPlanetGoodsPrices.getPrice(index);
        }

        return new RatingBuy(rating, toBuy);
    }

    /**
     * Represents a trading plan with the best path, rating, and items to buy.
     */
    private record TraderPlan(Deque<Integer> path, int rating, int[] toBuy) {}

    /**
     * Represents the rating and items to buy on a planet.
     */
    private record RatingBuy(int rating, int[] toBuy) {}
}
