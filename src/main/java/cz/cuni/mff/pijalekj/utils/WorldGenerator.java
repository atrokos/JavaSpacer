package cz.cuni.mff.pijalekj.utils;

import com.moandjiezana.toml.Toml;
import cz.cuni.mff.pijalekj.builders.EntityBuilder;
import cz.cuni.mff.pijalekj.builders.PlanetBuilder;
import cz.cuni.mff.pijalekj.entities.Entity;
import cz.cuni.mff.pijalekj.entities.Planet;
import cz.cuni.mff.pijalekj.enums.EntityType;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * The WorldGenerator class provides methods for generating a world, including planets, locations, and entities.
 * It utilizes randomization and data from a configuration file to create a dynamic and varied game environment.
 */
public class WorldGenerator {
    private final static Random generator = new Random();
    private final static int minDistance;
    private final static int maxDistance;
    private final static int minNeighbourCount;
    private final static int maxNeighbourCount;

    static {
        Toml defaultWorldData = new Toml().read(new File("./src/data/worldData.toml"));
        minDistance = defaultWorldData.getLong("Distances.Min").intValue();
        maxDistance = defaultWorldData.getLong("Distances.Max").intValue();
        minNeighbourCount = defaultWorldData.getLong("NeighbourCount.Min").intValue();
        maxNeighbourCount = defaultWorldData.getLong("NeighbourCount.Max").intValue();
    }

    /**
     * Generates an array of random planets with specified size.
     *
     * @param size The number of planets to generate.
     * @return An array of Planet objects representing the generated planets.
     */
    private static Planet[] generatePlanets(int size) {
        return IntStream.range(0, size)
                .mapToObj(PlanetBuilder::randomPlanet)
                .toArray(Planet[]::new);
    }

    /**
     * Generates a LocationsManager with random planets, connections, and entities based on the specified size.
     *
     * @param size The number of planets in the generated world.
     * @return A LocationsManager containing the generated world information.
     */
    public static LocationsManager generateLocations(int size) {
        Planet[] planets = generatePlanets(size);
        int[][] adjacencyMatrix = new int[size][size];
        List<List<HashSet<Integer>>> presentEntities = new ArrayList<>();
        List<HashSet<Integer>> neighborList = new ArrayList<>();

        for (int ID = 0; ID < size; ++ID) {
            presentEntities.add(new ArrayList<>(size));
            for (int ID2 = 0; ID2 < size; ++ID2) {
                presentEntities.get(ID).add(new HashSet<>());
            }
            neighborList.add(new HashSet<>());

            if (ID != 0) {
                connectPlanets(adjacencyMatrix, neighborList, ID, ID - 1);
            }
        }

        for (int ID = 0; ID < size; ++ID) {
            int currNoOfNeigh = neighborList.get(ID).size();
            if (ID == planets.length - 1) {
                currNoOfNeigh = 5; // So that in the next line, the last planet has 0 new neighbors
            }

            if (maxNeighbourCount - currNoOfNeigh <= minNeighbourCount) {
                continue;
            }
            int newNoOfNeigh = generator.nextInt(minNeighbourCount, maxNeighbourCount - currNoOfNeigh);

            for (int IDOffset = 2; IDOffset < newNoOfNeigh; ++IDOffset) {
                if (ID + IDOffset >= size) {
                    break;
                }
                connectPlanets(adjacencyMatrix, neighborList, ID, ID + IDOffset);
            }
        }

        List<Integer[]> neighborListArray = new ArrayList<>();
        for (var entry : neighborList) {
            neighborListArray.add(entry.toArray(new Integer[0]));
        }

        return new LocationsManager(adjacencyMatrix, presentEntities, neighborListArray, planets);
    }

    /**
     * Connects two planets in the world by updating the adjacency matrix and neighbor lists.
     *
     * @param adjMatrix The adjacency matrix representing connections between planets.
     * @param neighbors The list of neighbor sets for each planet.
     * @param planet1ID The ID of the first planet to connect.
     * @param planet2ID The ID of the second planet to connect.
     */
    private static void connectPlanets(int[][] adjMatrix, List<HashSet<Integer>> neighbors,
                                       int planet1ID, int planet2ID)
    {
        int distance = generator.nextInt(minDistance, maxDistance + 1);
        adjMatrix[planet1ID][planet2ID] = distance;
        adjMatrix[planet2ID][planet1ID] = distance;
        neighbors.get(planet2ID).add(planet1ID);
        neighbors.get(planet1ID).add(planet2ID);
    }

    /**
     * Generates entities, including police, traders, and pirates, and sets them in the EntityManager.
     *
     * @param lm The LocationsManager containing the world information.
     * @param em The EntityManager to populate with generated entities.
     * @param cm The CriminalsManager to associate with generated entities.
     */
    public static void generateEntities(LocationsManager lm, EntityManager em, CriminalsManager cm) {
        int noOfPlanets = lm.getAllPlanets().length;
        ArrayList<Entity> entities = new ArrayList<>();
        EntityBuilder eb = new EntityBuilder(em, lm, cm);

        int idCounter = 0; // Change to 1 after testing NPCs
        for (int i = 0; i < noOfPlanets; ++i) {
            entities.add(eb.newEntity(idCounter++, i, EntityType.Police));
            entities.add(eb.newEntity(idCounter++, i, EntityType.Trader));
            if (i % 5 == 0) {
                entities.add(eb.newEntity(idCounter++, i, EntityType.Pirate));
            }
        }
        em.setEntities(entities.toArray(Entity[]::new));
        em.setPlayer(eb.newPlayer(-1, noOfPlanets / 2, "NOTSET"));
    }

    /**
     * Populates the world by adding entities to their respective locations in the LocationsManager.
     *
     * @param lm The LocationsManager containing the world information.
     * @param em The EntityManager containing the entities to be added to the world.
     */
    public static void populateWorld(LocationsManager lm, EntityManager em) {
        for (var entity : em.getEntities()) {
            lm.addEntityTo(entity.getID(), entity.getCurrPosition());
        }
    }
}

