package cz.cuni.mff.pijalekj.Builders;

import cz.cuni.mff.pijalekj.entities.GoodsPrices;
import cz.cuni.mff.pijalekj.entities.Planet;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collector;

import static cz.cuni.mff.pijalekj.Builders.GoodsBuilder.defaultGoodsPrices;

public class PlanetBuilder {
    // Maybe unnecessary
    private final static String[] planetNames;

    static {
        try (var reader = Files.newBufferedReader(Path.of("./data/PlanetNames.txt"), StandardCharsets.UTF_8)) {
            planetNames = (String[]) reader.lines().toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Planet buildPlanet(PlanetIndustryType type, String name, int ID) {
        var goodsPrices = defaultGoodsPrices(type);
        return new Planet(name, type, ID, goodsPrices);
    }
}
