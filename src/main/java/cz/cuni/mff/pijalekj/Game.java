package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.entities.Entity;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;

public class Game {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();

    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        this.locationsManager = lm;
        this.criminalsManager = cm;
        this.entityManager = em;
    }

    public boolean play() {
        if (clock.tick()) {
            entityManager.resetNPCs(criminalsManager, locationsManager);
            locationsManager.updateAllPlanets();
            locationsManager.bigCheck(entityManager.getEntities().size());
        }

        boolean state = entityManager.play();
        criminalsManager.updateCriminals();
        return state;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public LocationsManager getLocationsManager() {
        return locationsManager;
    }

    public GameClock getClock() {
        return clock;
    }
}
