package cz.cuni.mff.pijalekj.managers;
import cz.cuni.mff.pijalekj.entities.Planet;


import java.util.ArrayList;
import java.util.HashSet;

public class LocationsManager {
    private ArrayList<ArrayList<Integer>> adjacencyMatrix;
    private ArrayList<ArrayList<HashSet<Integer>>> presentEntities;
    private ArrayList<HashSet<Integer>> neighborList;
    private ArrayList<Planet> planets;

    public LocationsManager(ArrayList<ArrayList<Integer>> adjacencyMatrix,
                            ArrayList<ArrayList<HashSet<Integer>>> presentEntities,
                            ArrayList<HashSet<Integer>> neighborList,
                            ArrayList<Planet> planets)
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
        if (planetID1 > adjacencyMatrix.size() || planetID2 > adjacencyMatrix.getFirst().size()) {
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");
        }
        return adjacencyMatrix.get(planetID1).get(planetID2) > 0;
    }

    public int getDistanceBetween(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > adjacencyMatrix.size() || planetIDTo > adjacencyMatrix.getFirst().size())
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");

        return adjacencyMatrix.get(planetIDFrom).get(planetIDTo);
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
        if (planetID >= planets.size()) // NOTE Originally was ">", replace it if a bug occurs
            throw new IllegalArgumentException("LocationsManager was given an incorrect planetID!");

        return planets.get(planetID);
    }

    public ArrayList<Planet> getAllPlanets() {
        return planets;
    }

    private void updateAllPlanets() {
        planets.stream().parallel().forEach(Planet::update);
    }
}
