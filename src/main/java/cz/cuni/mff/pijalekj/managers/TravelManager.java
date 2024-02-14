package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Planet;

import java.util.HashSet;

public class TravelManager {
    private final LocationsManager locMan;
    private final int ownerID;
    private int currLocID = -1;
    private int nextLocID = -1;
    private int travelTimeLeft = 0;

    public TravelManager(LocationsManager locMan, int currLocID, int ownerID) {
        this.locMan = locMan;
        this.currLocID = currLocID;
        this.ownerID = ownerID;
    }

    public void travel() {
        if (!isTraveling()) {
            return;
        }

        --travelTimeLeft;
        if (travelTimeLeft <= 0) {
            travelEnd();
        }
    }

    public void travelStart(int nextLocationID) {
        this.nextLocID = nextLocationID;
        this.travelTimeLeft = locMan.getDistanceBetween(this.currLocID, this.nextLocID);
        if (this.travelTimeLeft <= 0) {
            throw new IllegalArgumentException(
                    "NPC wants to travel to either the same place or those two places aren't neighbors");
        }

        this.locMan.removeEntityFrom(this.ownerID, this.currLocID);
        this.locMan.addEntityTo(this.ownerID, this.currLocID, this.nextLocID);
    }

    private void travelEnd() {
        this.locMan.removeEntityFrom(this.ownerID, this.currLocID, this.nextLocID);
        this.locMan.addEntityTo(this.ownerID, this.nextLocID);

        this.currLocID = this.nextLocID;
        this.nextLocID = -1;
    }

    public boolean isTraveling() {
        return this.travelTimeLeft > 0;
    }

    public HashSet<Integer> getPresentEntities() {
        if (isTraveling()) {
            return this.locMan.getPresentEntities(this.currLocID, this.nextLocID);
        }
        return this.locMan.getPresentEntities(this.currLocID);
    }

    public Planet getPlanet(int planetID) {
        return this.locMan.getPlanet(planetID);
    }

    public HashSet<Integer> getNeighbors(int planetID) {
        return this.locMan.getNeighbors(planetID);
    }

    public HashSet<Integer> getNeighbors() {
        return getNeighbors(currLocID);
    }

    public Planet getCurrLocation() {
        return this.locMan.getPlanet(this.currLocID);
    }

    public Planet getNextLocation() {
        return this.locMan.getPlanet(this.nextLocID);
    }

    public int getNextLocationID() {
        return this.nextLocID;
    }

    public int getCurrLocationID() {
        return this.currLocID;
    }

    public int getDistanceBetween(int first, int second) {
        return this.locMan.getDistanceBetween(first, second);
    }
}
