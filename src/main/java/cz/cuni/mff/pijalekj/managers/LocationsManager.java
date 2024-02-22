package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Planet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LocationsManager {
    private final int[][] adjacencyMatrix;
    private final List<List<HashSet<Integer>>> presentEntities;
    private final List<Integer[]> neighborList;
    private final Planet[] planets;

    public LocationsManager(int[][] adjacencyMatrix,
                            List<List<HashSet<Integer>>> presentEntities,
                            List<Integer[]> neighborList,
                            Planet[] planets)
    {
        this.adjacencyMatrix = adjacencyMatrix;
        this.presentEntities = presentEntities;
        this.neighborList = neighborList;
        this.planets = planets;
    }

    public Integer[] getNeighborsOf(Integer planetID) {
        return this.neighborList.get(planetID);
    }

    public boolean isNeighborOf(int planetID1, int planetID2) {
        if (planetID1 > this.adjacencyMatrix.length || planetID2 > this.adjacencyMatrix[0].length) {
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");
        }
        return this.adjacencyMatrix[planetID1][planetID2] > 0;
    }

    public int getDistanceBetween(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > this.adjacencyMatrix.length || planetIDTo > this.adjacencyMatrix[0].length)
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");

        return this.adjacencyMatrix[planetIDFrom][planetIDTo];
    }

    public void addEntityTo(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > this.presentEntities.size() || planetIDTo > this.presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        this.presentEntities.get(planetIDFrom).get(planetIDTo).add(entityID);
    }

    public void removeEntityFrom(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > this.presentEntities.size() || planetIDTo > this.presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        this.presentEntities.get(planetIDFrom).get(planetIDTo).remove(entityID);
    }

    public HashSet<Integer> getPresentEntities(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > this.presentEntities.size() || planetIDTo > this.presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");

        var first = this.presentEntities.get(planetIDFrom).get(planetIDTo);
        if (planetIDFrom == planetIDTo) {
            return first;
        }

        var second = this.presentEntities.get(planetIDTo).get(planetIDFrom);
        first.addAll(second);

        return first;
    }

    public HashSet<Integer> getPresentEntities(int planetID) {
        return this.getPresentEntities(planetID, planetID);
    }

    public void addEntityTo(int entityID, int planetID) {
        this.addEntityTo(entityID, planetID, planetID);
    }

    public void removeEntityFrom(int entityID, int planetID) {
        this.removeEntityFrom(entityID, planetID, planetID);
    }

    public Planet getPlanet(int planetID) {
        if (planetID >= this.planets.length) // NOTE Originally was ">", replace it if a bug occurs
            throw new IllegalArgumentException("LocationsManager was given an incorrect planetID!");

        return this.planets[planetID];
    }

    public Planet[] getAllPlanets() {
        return this.planets;
    }

    public void updateAllPlanets() {
        Arrays.stream(this.planets).parallel().forEach(Planet::update);
    }

    public void bigCheck(int numOfEntities) {
        // Check that all planets know their neighbors
        for (int i = 0; i < this.adjacencyMatrix.length; ++i) {
            for (int j = 0; j < this.adjacencyMatrix[0].length; ++j) {
                assert this.adjacencyMatrix[i][j] == this.adjacencyMatrix[j][i];
            }
        }

        // Check that every entity is present exactly once
        HERE: for (int ID = 0; ID < numOfEntities; ++ID) {
            for (int i = 0; i < this.adjacencyMatrix.length; ++i) {
                for (int j = 0; j < this.adjacencyMatrix[0].length; ++j) {
                    if (this.presentEntities.get(i).get(j).contains(ID)) {
                        continue HERE;
                    }
                }
            }
            throw new RuntimeException("Entity with ID " + ID + " was not found!");
        }
    }

    public String getPlanetName(int ID) {
        return planets[ID].getName();
    }
}
