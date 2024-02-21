package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

public class Player {
    public final String name;
    private final TravelManager travelManager;
    public Ship ownedShip;
    public final EntityStats entityStats;
    public Player(TravelManager travelManager, Ship ownedShip, EntityStats entityStats, String name) {
        this.name = name;
        this.travelManager = travelManager;
        this.entityStats = entityStats;
    }

    public boolean isDead() {
        return this.ownedShip.getStats().health.getCurr() > 0;
    }
}
