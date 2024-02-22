package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.Arrays;

public class Player {
    public final String name;
    private final TravelManager travelManager;
    public Ship ownedShip;
    public final EntityStats entityStats;
    public Player(TravelManager travelManager, Ship ownedShip, EntityStats entityStats, String name) {
        this.name = name;
        this.travelManager = travelManager;
        this.entityStats = entityStats;
        this.ownedShip = ownedShip;
    }
    public boolean isAlive() {
        return this.ownedShip.getStats().health.getCurr() > 0;
    }
    public int getCurrFuel() {
        return this.ownedShip.getStats().fuel.getCurr();
    }
    public int getMaxFuel() {
        return this.ownedShip.getStats().fuel.getMax();
    }
    public int getCurrCargo() {
        return this.entityStats.getTotalGoodsAmount();
    }
    public int getMaxCargo() {
        return this.ownedShip.getStats().maxCargo;
    }
    public int getCredits() {
        return this.entityStats.getCredits();
    }
    public int getCurrHull() {
        return this.ownedShip.getStats().health.getCurr();
    }
    public int getMaxHull() {
        return this.ownedShip.getStats().health.getMax();
    }
    public Planet getCurrPlanet() {
        return this.travelManager.getCurrLocation();
    }
    public Planet getNextPlanet() {
        return this.travelManager.getNextLocation();
    }
}
