package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.*;

public class Trader extends Entity {
    private Deque<Integer> path = new LinkedList<>();
    public Trader(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);
    }

    @Override
    public BattleDecision battle(Entity opponent) {
        return new BattleDecision(BattleActionType.flee, this.ownedShip.getFleeChance());
    }

    @Override
    public void won(Entity opponent) {
        this.takeAll(opponent.getEntityStats());
        this.entityStats.transferAllCredits(opponent.getEntityStats());
    }

    @Override
    public void lost() {

    }

    @Override
    public OptionalInt play() {

        if (!this.isAlive()) {
            return OptionalInt.empty();
        }

        if (this.travelManager.isTraveling()) {
            this.travel();
            return OptionalInt.empty();
        }

        this.maintenance();
        if (this.isFull()) {
            this.sell();
        }

        if (this.path.isEmpty()) {
            if (this.prevAction != EntityActions.sell) {
                this.sell();
                this.prevAction = EntityActions.sell;
                return OptionalInt.empty();
            } else if (this.ownedShip.getStats().fuel.getCurr() < 13) {
                return OptionalInt.empty();
            } else {
                this.createPlan();
            }
        } else {
            int nextDestinationID = this.path.pop();
            this.travelManager.travelStart(nextDestinationID);
            this.prevAction = EntityActions.travelPrep;
        }
        return OptionalInt.empty();
    }

    private void createPlan() {
        Deque<Integer> newPath = new LinkedList<>();
        newPath.add(this.travelManager.getCurrLocationID());

        HashSet<Integer> visited = new HashSet<>();
        var results = this.findBestPlanet(newPath, 0, Integer.MIN_VALUE, visited, -1);
        var currPlanet = this.travelManager.getCurrLocation();

        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            this.entityStats.removeCredits(currPlanet.buy(index, results.toBuy[index]));
            this.entityStats.addGood(index, results.toBuy[index]);
        }

        results.path.removeFirst();
        this.path = results.path;
        this.travelManager.travelStart(this.path.removeFirst());
    }

    private TraderPlan findBestPlanet(Deque<Integer> path, int travelLength, int rating,
                                      HashSet<Integer> visited, int counter)
    {
        int currPlanetID = path.getLast();
        int bestRating = rating;
        Deque<Integer> bestPath = new LinkedList<Integer>(path);
        int[] toBuy = new int[9];

        ++counter;
        visited.add(currPlanetID);

        if (currPlanetID != this.travelManager.getCurrLocationID()) {
            var planetRating = this.ratePlanet(this.travelManager.getPlanet(currPlanetID), travelLength);
            if (planetRating.rating > bestRating) {
                bestRating= planetRating.rating;
                toBuy = planetRating.toBuy;
            }
        }

        if (counter == 3) {
            return new TraderPlan(bestPath, bestRating, toBuy);
        }

        for (int neighborID : this.travelManager.getNeighbors(currPlanetID)) {
            if (visited.contains(neighborID)) {
                continue;
            }

            path.addLast(neighborID);
            int distance = travelLength + this.travelManager.getDistanceBetween(currPlanetID, neighborID);
            var found = this.findBestPlanet(path, distance, bestRating, visited, counter);
            path.removeLast();

            if (found.rating > bestRating) {
                bestRating = found.rating;
                bestPath = found.path;
                toBuy = found.toBuy;
            }
        }

        return new TraderPlan(bestPath, bestRating, toBuy);
    }

    private RatingBuy ratePlanet(Planet planet, int travelLength) {
        int[] toBuy = new int[9];
        SortedMap<Integer, Integer> goodsDiff = new TreeMap<>();
        var currPlanetGoodsPrices = this.travelManager.getCurrLocation().getGoodsPrices();
        var planetGoodsPrices = planet.getGoodsPrices();
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

        int credits = this.entityStats.getCredits();
        int freeSpace = this.ownedShip.getStats().maxCargo - this.entityStats.getTotalGoodsAmount();
        for (var key : goodsDiff.reversed().keySet()) {
            int index = 0;
            try {
                index = goodsDiff.get(key);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Key: " + key);
                throw new RuntimeException();
            }
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

    private record TraderPlan(Deque<Integer> path, int rating, int[] toBuy) {}
    private record RatingBuy(int rating, int[] toBuy) {}
}
