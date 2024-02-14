package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Planet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LocationsManager {
    private final int[][] adjacencyMatrix;
    private final List<List<HashSet<Integer>>> presentEntities;
    private final List<HashSet<Integer>> neighborList;
    private final Planet[] planets;

    public LocationsManager(int[][] adjacencyMatrix,
                            List<List<HashSet<Integer>>> presentEntities,
                            List<HashSet<Integer>> neighborList,
                            Planet[] planets)
    {
        this.adjacencyMatrix = adjacencyMatrix;
        this.presentEntities = presentEntities;
        this.neighborList = neighborList;
        this.planets = planets;
    }

    public HashSet<Integer> getNeighbors(Integer planetID) {
        return this.neighborList.get(planetID);
    }

    public boolean isNeighborOf(int planetID1, int planetID2) {
        if (planetID1 > adjacencyMatrix.length || planetID2 > adjacencyMatrix[0].length) {
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");
        }
        return adjacencyMatrix[planetID1][planetID2] > 0;
    }

    public int getDistanceBetween(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > adjacencyMatrix.length || planetIDTo > adjacencyMatrix[0].length)
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");

        return adjacencyMatrix[planetIDFrom][planetIDTo];
    }

    public void addEntityTo(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > presentEntities.size() || planetIDTo > presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        presentEntities.get(planetIDFrom).get(planetIDTo).add(entityID);
    }

    public void removeEntityFrom(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > presentEntities.size() || planetIDTo > presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        presentEntities.get(planetIDFrom).get(planetIDTo).remove(entityID);
    }

    public HashSet<Integer> getPresentEntities(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > presentEntities.size() || planetIDTo > presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");

        var first = presentEntities.get(planetIDFrom).get(planetIDTo);
        if (planetIDFrom == planetIDTo) {
            return first;
        }

        var second = presentEntities.get(planetIDTo).get(planetIDFrom);
        first.addAll(second);

        return first;
    }

    public HashSet<Integer> getPresentEntities(int planetID) {
        return getPresentEntities(planetID, planetID);
    }

    public void addEntityTo(int entityID, int planetID) {
        this.addEntityTo(entityID, planetID, planetID);
    }

    public void removeEntityFrom(int entityID, int planetID) {
        this.removeEntityFrom(entityID, planetID, planetID);
    }

    public Planet getPlanet(int planetID) {
        if (planetID >= planets.length) // NOTE Originally was ">", replace it if a bug occurs
            throw new IllegalArgumentException("LocationsManager was given an incorrect planetID!");

        return planets[planetID];
    }

    public Planet[] getAllPlanets() {
        return planets;
    }

    public void updateAllPlanets() {
        Arrays.stream(planets).parallel().forEach(Planet::update);
    }
}
