package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.utils.WorldGenerator;

public class Main {
    public static void main(String[] args) {
        EntityManager em = new EntityManager();
        LocationsManager lm = WorldGenerator.generateLocations(500);
        CriminalsManager cm = new CriminalsManager();

        WorldGenerator.generateEntities(lm, em, cm);
        WorldGenerator.populateWorld(lm, em);
        Game game = new Game(lm, cm, em);
        boolean state = true;
        while (state) {
            state = game.play();
        }
    }
}
