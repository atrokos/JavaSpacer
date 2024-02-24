package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Planet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Manages the locations, entities, and relationships between planets in a system.
 */
public class LocationsManager {
    private final int[][] adjacencyMatrix;
    private final List<List<HashSet<Integer>>> presentEntities;
    private final List<Integer[]> neighborList;
    private final Planet[] planets;

    /**
     * Constructs a LocationsManager with the given parameters.
     *
     * @param adjacencyMatrix    The adjacency matrix representing the relationships between planets.
     * @param presentEntities    The list of entities present on each pair of planets.
     * @param neighborList       The list of neighbors for each planet.
     * @param planets            The array of Planet objects representing each planet.
     */
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

    /**
     * Retrieves the neighbors of a specific planet.
     *
     * @param planetID   The ID of the planet.
     * @return           An array of Integer representing the neighbors of the specified planet.
     */
    public Integer[] getNeighborsOf(Integer planetID) {
        return neighborList.get(planetID);
    }

    /**
     * Gets the distance between two planets.
     *
     * @param planetIDFrom   The ID of the source planet.
     * @param planetIDTo     The ID of the destination planet.
     * @return               The distance between the specified planets.
     * @throws IllegalArgumentException if the planet IDs are incorrect.
     */
    public int getDistanceBetween(int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > adjacencyMatrix.length || planetIDTo > adjacencyMatrix[0].length)
            throw new IllegalArgumentException("Adjacency matrix was given incorrect ID(s)");

        return adjacencyMatrix[planetIDFrom][planetIDTo];
    }
    /**
     * Adds an entity to the relationship between two planets.
     *
     * @param entityID      The ID of the entity.
     * @param planetIDFrom  The ID of the source planet.
     * @param planetIDTo    The ID of the destination planet.
     * @throws IllegalArgumentException if the planet IDs are incorrect.
     */
    public void addEntityTo(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > presentEntities.size() || planetIDTo > presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        presentEntities.get(planetIDFrom).get(planetIDTo).add(entityID);
    }
    /**
     * Removes an entity from the relationship between two planets.
     *
     * @param entityID      The ID of the entity.
     * @param planetIDFrom  The ID of the source planet.
     * @param planetIDTo    The ID of the destination planet.
     * @throws IllegalArgumentException if the planet IDs are incorrect.
     */
    public void removeEntityFrom(int entityID, int planetIDFrom, int planetIDTo) {
        if (planetIDFrom > presentEntities.size() || planetIDTo > presentEntities.getFirst().size())
            throw new IllegalArgumentException("LocationsManager was given an incorrect planet ID!");
        presentEntities.get(planetIDFrom).get(planetIDTo).remove(entityID);
    }
    /**
     * Gets the set of entities present on the path between two planets.
     *
     * @param planetIDFrom  The ID of the source planet.
     * @param planetIDTo    The ID of the destination planet.
     * @return              The set of entities present on the specified relationship.
     * @throws IllegalArgumentException if the planet IDs are incorrect.
     */
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
    /**
     * Gets the set of entities present on a planet.
     *
     * @param planetID   The ID of the planet.
     * @return           The set of entities present on the specified planet.
     */
    public HashSet<Integer> getPresentEntities(int planetID) {
        return getPresentEntities(planetID, planetID);
    }
    /**
     * Adds an entity to a planet.
     *
     * @param entityID   The ID of the entity.
     * @param planetID   The ID of the planet.
     */
    public void addEntityTo(int entityID, int planetID) {
        addEntityTo(entityID, planetID, planetID);
    }
    /**
     * Removes an entity from a planet.
     *
     * @param entityID   The ID of the entity.
     * @param planetID   The ID of the planet.
     */
    public void removeEntityFrom(int entityID, int planetID) {
        removeEntityFrom(entityID, planetID, planetID);
    }
    /**
     * Gets the Planet object for a specific planet ID.
     *
     * @param planetID   The ID of the planet.
     * @return           The Planet object representing the specified planet.
     * @throws IllegalArgumentException if the planet ID is incorrect.
     */
    public Planet getPlanet(int planetID) {
        if (planetID >= planets.length) // NOTE Originally was ">", replace it if a bug occurs
            throw new IllegalArgumentException("LocationsManager was given an incorrect planetID!");

        return planets[planetID];
    }
    /**
     * Gets an array of all Planet objects in the system.
     *
     * @return   An array of all Planet objects.
     */
    public Planet[] getAllPlanets() {
        return planets;
    }
    /**
     * Updates all planets in parallel by invoking their update method.
     */
    public void updateAllPlanets() {
        Arrays.stream(planets).parallel().forEach(Planet::update);
    }
    /**
     * Gets the name of a planet based on its ID.
     *
     * @param ID   The ID of the planet.
     * @return     The name of the specified planet.
     */
    public String getPlanetName(int ID) {
        return planets[ID].name();
    }
}
