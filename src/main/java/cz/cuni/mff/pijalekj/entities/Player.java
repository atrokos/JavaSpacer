package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.Playerlike;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.HashSet;

/**
 * The Player class represents the player in the game, implementing the Playerlike interface.
 * It includes information about the player's name, owned ship, entity stats, and various game-related attributes.
 */
public class Player implements Playerlike {
    public String name;
    private final TravelManager travelManager;
    public Ship ownedShip;
    public final EntityStats entityStats;

    /**
     * Constructs a Player object with specified attributes.
     *
     * @param travelManager The TravelManager for managing travel-related functionalities.
     * @param ownedShip     The Ship owned by the player.
     * @param entityStats   The EntityStats representing the player's statistics and inventory.
     * @param name          The name of the player.
     */
    public Player(TravelManager travelManager, Ship ownedShip, EntityStats entityStats, String name) {
        this.name = name;
        this.travelManager = travelManager;
        this.entityStats = entityStats;
        this.ownedShip = ownedShip;
    }

    /**
     * Destroys the player's ship, marking the player as killed.
     */
    public void kill() {
        ownedShip.destroy();
    }

    /**
     * Checks if the player is alive based on the current hull of the owned ship.
     *
     * @return True if the player is alive, false otherwise.
     */
    public boolean isAlive() {
        return ownedShip.getStats().hull.getCurr() > 0;
    }

    /**
     * Gets the current fuel level of the player's ship.
     *
     * @return The current fuel level.
     */
    public int getCurrFuel() {
        return ownedShip.getStats().fuel.getCurr();
    }

    /**
     * Gets the maximum fuel capacity of the player's ship.
     *
     * @return The maximum fuel capacity.
     */
    public int getMaxFuel() {
        return ownedShip.getStats().fuel.getMax();
    }

    /**
     * Gets the current cargo amount carried by the player's ship.
     *
     * @return The current cargo amount.
     */
    public int getCurrCargo() {
        return entityStats.getTotalGoodsAmount();
    }

    /**
     * Gets the maximum cargo capacity of the player's ship.
     *
     * @return The maximum cargo capacity.
     */
    public int getMaxCargo() {
        return ownedShip.getStats().maxCargo;
    }

    /**
     * Gets the current credits owned by the player.
     *
     * @return The current credits.
     */
    public int getCredits() {
        return entityStats.getCredits();
    }

    /**
     * Gets the current hull health of the player's ship.
     *
     * @return The current hull health.
     */
    public int getCurrHull() {
        return ownedShip.getStats().hull.getCurr();
    }

    /**
     * Gets the maximum hull health of the player's ship.
     *
     * @return The maximum hull health.
     */
    public int getMaxHull() {
        return ownedShip.getStats().hull.getMax();
    }

    /**
     * Gets the current planet where the player is located.
     *
     * @return The current planet.
     */
    public Planet getCurrPlanet() {
        return travelManager.getCurrLocation();
    }

    /**
     * Gets the next planet in the player's travel path.
     *
     * @return The next planet.
     */
    public Planet getNextPlanet() {
        return travelManager.getNextLocation();
    }

    /**
     * Sets the name of the player.
     *
     * @param name The new name for the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Initiates travel to a neighboring location specified by the neighbor ID.
     *
     * @param neighborID The ID of the neighboring location.
     */
    public void travelTo(int neighborID) {
        travelManager.travelStart(neighborID);
    }

    /**
     * Checks if the player is currently traveling.
     *
     * @return True if the player is traveling, false otherwise.
     */
    public boolean isTraveling() {
        return travelManager.isTraveling();
    }

    /**
     * Initiates the travel process, burning fuel and updating travel status.
     */
    public void travel() {
        travelManager.travel();
        ownedShip.burnFuel();
    }

    /**
     * Gets the set of entity IDs present at the current location of the player.
     *
     * @return The set of entity IDs.
     */
    public HashSet<Integer> getPresentEntities() {
        return travelManager.getPresentEntities();
    }

    /**
     * Gets the owned ship of the player.
     *
     * @return The owned ship.
     */
    @Override
    public Ship getOwnedShip() {
        return ownedShip;
    }

    /**
     * Gets the entity stats of the player.
     *
     * @return The entity stats.
     */
    @Override
    public EntityStats getEntityStats() {
        return entityStats;
    }
}
