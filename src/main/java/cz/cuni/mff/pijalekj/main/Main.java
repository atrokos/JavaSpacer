package cz.cuni.mff.pijalekj.main;

import cz.cuni.mff.pijalekj.Game;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.utils.WorldGenerator;

public class Main {
    public static void main(String... args) {
        EntityManager em = new EntityManager();
        LocationsManager lm = WorldGenerator.generateLocations(500);
        CriminalsManager cm = new CriminalsManager();

        WorldGenerator.generateEntities(lm, em, cm);
        WorldGenerator.populateWorld(lm, em);
        Game game = new Game(lm, cm, em);

        try {
            while (game.play()) {
                continue;
            }
        } catch (Exception e) {
            System.out.printf("Fatal error: The game cannot recover. Exception details:\n%s", e.getMessage());
            System.err.printf("Fatal error: The game cannot recover. Exception details:\n%s", e.getMessage());
        }
    }
}
