package cz.cuni.mff.pijalekj.builders;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.entities.*;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.EntityType;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;

public class EntityBuilder {
    private final EntityManager entityManager;
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final static String defaultShipKey = "Entities.DefaultShips.";
    private final static String startCreditsKey = "Entities.StartCredits.";

    public EntityBuilder(EntityManager entityManager, LocationsManager locationsManager,
                         CriminalsManager criminalsManager)
    {
        this.entityManager = entityManager;
        this.locationsManager = locationsManager;
        this.criminalsManager = criminalsManager;
    }

    public Entity newEntity(int ID, int currLocationID, EntityType type) {
        var shipType = ShipType.valueOf(Constants.builders.getString(defaultShipKey + type));
        var ship = ShipBuilder.buildShip(shipType);

        var goods = new int[9];
        var credits = Constants.builders.getLong(startCreditsKey + type).intValue();
        var stats = new EntityStats(credits, goods);
        var travelManager = new TravelManager(locationsManager, currLocationID, ID);
        var prevAction = EntityActions.none;

        return switch (type) {
            case Player -> new Player(travelManager, entityManager, criminalsManager,
                        ship, stats, prevAction, ID, "NOTSET");
            case Pirate -> new Pirate(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID);
            case Trader -> new Trader(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID);
            case Police -> new Police(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID, currLocationID);
            default -> throw new RuntimeException("Unknown EntityType received!");
        };
    }
}
