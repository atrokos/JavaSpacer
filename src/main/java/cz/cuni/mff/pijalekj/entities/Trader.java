package cz.cuni.mff.pijalekj.entities;

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
    private LinkedList<Integer> path = new LinkedList<>();
    public Trader(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);
    }

    @Override
    public BattleActionType battle(Entity opponent) {
        return BattleActionType.flee;
    }

    @Override
    public void won(Entity opponent) {
        takeAll(opponent);
        entityStats.credits += opponent.getEntityStats().credits;
        opponent.getEntityStats().credits = 0;
    }

    @Override
    public void lost() {

    }

    @Override
    public void play() {
        System.out.println("Trader is playing!");
        if (!this.isAlive()) {
            return;
        }

        if (travelManager.isTraveling()) {
            travel();
            return;
        }

        maintenance();
        if (!this.isEmpty()) {
            sell();
        }

        if (path.isEmpty()) {
            if (prevAction != EntityActions.sell) {
                sell();
                prevAction = EntityActions.sell;
                return;
            } else if (ownedShip.getStats().fuel.getCurr() < 13) {
                return;
            } else {
                createPlan();
            }
        } else {
            int nextDestinationID = path.pop();
            travelManager.travelStart(nextDestinationID);
            prevAction = EntityActions.travelPrep;
        }
    }

    private void createPlan() {
        LinkedList<Integer> newPath = new LinkedList<>();
        newPath.add(travelManager.getCurrLocationID());

        HashSet<Integer> visited = new HashSet<>();
        var results = findBestPlanet(newPath, 0, Integer.MIN_VALUE, visited, -1);
        var currPlanet = travelManager.getCurrLocation();

        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            entityStats.credits -= currPlanet.buy(index, results.toBuy[index]);
            changeGoodsBy(index, results.toBuy[index]);
        }

        results.path.removeFirst();
        this.path = results.path;
        travelManager.travelStart(path.removeFirst());
    }

    private TraderPlan findBestPlanet(LinkedList<Integer> path, int travelLength, int rating,
                                      HashSet<Integer> visited, int counter)
    {
        int currPlanetID = path.getLast();
        int bestRating = rating;
        var bestPath = new LinkedList<Integer>(path);
        int[] toBuy = new int[9];

        ++counter;
        visited.add(currPlanetID);

        if (currPlanetID != travelManager.getCurrLocationID()) {
            var planetRating = ratePlanet(travelManager.getPlanet(currPlanetID), travelLength);
            if (planetRating.rating > bestRating) {
                bestRating= planetRating.rating;
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

    private RatingBuy ratePlanet(Planet planet, int travelLength) {
        int[] toBuy = new int[9];
        SortedMap<Integer, Integer> goodsDiff = new TreeMap<>();
        var currPlanetGoodsPrices = travelManager.getCurrLocation().getGoodsPrices();
        var planetGoodsPrices = planet.getGoodsPrices();
        int rating = -(travelLength * Constants.fuelCost);

        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            int priceDiff = planetGoodsPrices.prices[index] - currPlanetGoodsPrices.prices[index];

            while (goodsDiff.containsKey(priceDiff)) {
                --priceDiff;
            }
            if (priceDiff > 0)
                goodsDiff.put(priceDiff, index);
        }

        int credits = entityStats.credits;
        int freeSpace = ownedShip.getStats().cargo.getMax() - ownedShip.getStats().cargo.getCurr();
        for (var key : goodsDiff.keySet()) {
            int index = 0;
            try {
                index = goodsDiff.get(key);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Key: " + key);
                throw new RuntimeException();
            }
            int available = currPlanetGoodsPrices.goods[index];
            int affordable = credits / currPlanetGoodsPrices.prices[index];
            int buyable = Math.min(available, affordable);

            if (freeSpace - buyable <= 0) {
                toBuy[index] = freeSpace;
                rating += freeSpace * key;
                return new RatingBuy(rating, toBuy);
            }

            toBuy[index] = buyable;
            rating += buyable * key;
            freeSpace -= buyable;
            credits -= buyable * currPlanetGoodsPrices.prices[index];
        }

        return new RatingBuy(rating, toBuy);
    }

    private void sell() {
        var currPlanet = travelManager.getCurrLocation();
        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            entityStats.credits += currPlanet.sell(index, entityStats.ownedGoods[index]);
            changeGoodsBy(index, -entityStats.ownedGoods[index]);
        }
    }

    private void maintenance() {
        // Shields always recharge when at a planet
        ownedShip.rechargeShields();

        // Fuel
        int fuelDiff = ownedShip.getStats().fuel.getMax() - ownedShip.getStats().fuel.getCurr();
        int neededCredits = fuelDiff * Constants.fuelCost;

        if (fuelDiff > 0 && neededCredits <= entityStats.credits) {
            entityStats.credits -= neededCredits;
            ownedShip.refuel(fuelDiff);
        }
        else {
            entityStats.credits += 10;
        }

        // Hull
        int hullDiff = ownedShip.getStats().health.getMax() - ownedShip.getStats().health.getCurr();
        neededCredits = fuelDiff * Constants.fuelCost;
        if (hullDiff > 0 && neededCredits <= entityStats.credits) {
            entityStats.credits -= neededCredits;
            ownedShip.repairHull(hullDiff);
        }
        // TODO maybe add "else { + 10 }" if NPCs are too passive
    }

    private record TraderPlan(LinkedList<Integer> path, int rating, int[] toBuy) {}
    private record RatingBuy(int rating, int[] toBuy) {}
}
