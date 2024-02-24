package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Planet;

import java.util.HashSet;

/**
 * The TravelManager class represents a manager for handling travel between locations for a specific entity.
 */
public class TravelManager {
    private final LocationsManager locMan;
    private final int ownerID;
    private int currLocID;
    private int nextLocID = -1;
    private int travelTimeLeft = 0;

    /**
     * Constructs a TravelManager with the specified parameters.
     *
     * @param locMan   The LocationsManager responsible for managing locations and distances.
     * @param currLocID The current location ID where the entity is initially located.
     * @param ownerID   The owner ID associated with the entity using this TravelManager.
     */
    public TravelManager(LocationsManager locMan, int currLocID, int ownerID) {
        this.locMan = locMan;
        this.currLocID = currLocID;
        this.ownerID = ownerID;
    }

    /**
     * Simulates the passage of time during travel. If the entity is not currently traveling, the method returns immediately.
     */
    public void travel() {
        if (!isTraveling()) {
            return;
        }

        --travelTimeLeft;
        if (travelTimeLeft <= 0) {
            travelEnd();
        }
    }

    /**
     * Initiates travel to the specified location ID.
     *
     * @param nextLocationID The destination location ID to which the entity is traveling.
     * @throws IllegalArgumentException if the travel distance is non-positive or the destination is the same as the current location.
     */
    public void travelStart(int nextLocationID) {
        nextLocID = nextLocationID;
        travelTimeLeft = locMan.getDistanceBetween(currLocID, nextLocID);
        if (travelTimeLeft <= 0) {
            throw new IllegalArgumentException(
                    "NPC wants to travel to either the same place or those two places aren't neighbors");
        }

        locMan.removeEntityFrom(ownerID, currLocID);
        locMan.addEntityTo(ownerID, currLocID, nextLocID);
    }

    /**
     * Completes the travel by updating the location and cleaning up the entities' presence.
     */
    private void travelEnd() {
        locMan.removeEntityFrom(ownerID, currLocID, nextLocID);
        locMan.addEntityTo(ownerID, nextLocID);

        currLocID = nextLocID;
        nextLocID = -1;
    }

    /**
     * Checks if the entity is currently in the process of traveling.
     *
     * @return true if the entity is currently traveling; false otherwise.
     */
    public boolean isTraveling() {
        return travelTimeLeft > 0;
    }

    /**
     * Retrieves the set of entities present at the current or next location depending on the travel status.
     *
     * @return A HashSet of Integer representing entity IDs present at the current or next location.
     */
    public HashSet<Integer> getPresentEntities() {
        if (isTraveling()) {
            return locMan.getPresentEntities(currLocID, nextLocID);
        }
        return locMan.getPresentEntities(currLocID);
    }

    /**
     * Retrieves the Planet associated with the given planet ID.
     *
     * @param planetID The ID of the planet for which to retrieve information.
     * @return The Planet object representing the specified planet ID.
     */
    public Planet getPlanet(int planetID) {
        return locMan.getPlanet(planetID);
    }

    /**
     * Retrieves an array of neighbor planet IDs for the specified planet ID.
     *
     * @param planetID The ID of the planet for which to retrieve neighbor information.
     * @return An array of Integer representing the neighbor planet IDs.
     */
    public Integer[] getNeighbors(int planetID) {
        return locMan.getNeighborsOf(planetID);
    }

    /**
     * Retrieves an array of neighbor planet IDs for the current location.
     *
     * @return An array of Integer representing the neighbor planet IDs of the current location.
     */
    public Integer[] getNeighbors() {
        return getNeighbors(currLocID);
    }

    /**
     * Retrieves the Planet object associated with the current location.
     *
     * @return The Planet object representing the current location.
     */
    public Planet getCurrLocation() {
        return locMan.getPlanet(currLocID);
    }

    /**
     * Retrieves the Planet object associated with the next location (destination).
     *
     * @return The Planet object representing the next location.
     */
    public Planet getNextLocation() {
        return locMan.getPlanet(nextLocID);
    }

    /**
     * Retrieves the ID of the next location (destination).
     *
     * @return The ID of the next location.
     */
    public int getNextLocationID() {
        return nextLocID;
    }

    /**
     * Retrieves the ID of the current location.
     *
     * @return The ID of the current location.
     */
    public int getCurrLocationID() {
        return currLocID;
    }

    /**
     * Retrieves the distance between two specified locations.
     *
     * @param first  The ID of the first location.
     * @param second The ID of the second location.
     * @return The distance between the specified locations.
     */
    public int getDistanceBetween(int first, int second) {
        return locMan.getDistanceBetween(first, second);
    }
}
