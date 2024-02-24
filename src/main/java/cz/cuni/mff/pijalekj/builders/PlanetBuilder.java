package cz.cuni.mff.pijalekj.builders;

import cz.cuni.mff.pijalekj.entities.Planet;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static cz.cuni.mff.pijalekj.builders.GoodsBuilder.defaultGoodsPrices;

/**
 * The PlanetBuilder class provides static methods for constructing planet objects,
 * including methods to build planets based on industry types or generate random planets.
 */
public class PlanetBuilder {
    private final static Random generator = new Random();
    public final static String[] planetNames;

    static {
        try (var reader = Files.newBufferedReader(Path.of("./src/data/PlanetNames.txt"), StandardCharsets.UTF_8)) {
            planetNames = reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds a planet with the specified industry type, name, and ID.
     *
     * @param type  The planet industry type.
     * @param name  The name of the planet.
     * @param ID    The unique identifier for the planet.
     * @return      The constructed Planet object.
     */
    public static Planet buildPlanet(PlanetIndustryType type, String name, int ID) {
        var goodsPrices = defaultGoodsPrices(type);
        return new Planet(name, type, ID, goodsPrices);
    }

    /**
     * Generates a random planet with a random industry type, a randomly selected name,
     * and the specified ID.
     *
     * @param ID    The unique identifier for the planet.
     * @return      The randomly generated Planet object.
     */
    public static Planet randomPlanet(int ID) {
        int randomType = generator.nextInt(0, 101);
        int randomName = generator.nextInt(0, planetNames.length);

        String name = planetNames[randomName];

        if (randomType <= 50) {
            return buildPlanet(PlanetIndustryType.Agricultural, name, ID);
        } else if (randomType <= 80) {
            return buildPlanet(PlanetIndustryType.Industrial, name, ID);
        } else {
            return buildPlanet(PlanetIndustryType.Technological, name, ID);
        }
    }
}
