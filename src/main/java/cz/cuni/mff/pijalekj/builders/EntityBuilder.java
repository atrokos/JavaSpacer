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

/**
 * The EntityBuilder class is responsible for creating new entities in the game.
 * It uses predefined configurations to generate entities based on their types,
 * such as pirates, traders, police, and players.
 */
public class EntityBuilder {
    private final EntityManager entityManager;
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final static String defaultShipKey = "Entities.DefaultShips.";
    private final static String startCreditsKey = "Entities.StartCredits.";

    /**
     * Constructs an EntityBuilder with references to the entity manager, locations manager,
     * and criminals manager.
     *
     * @param entityManager      The entity manager responsible for managing entities.
     * @param locationsManager   The locations manager responsible for managing locations.
     * @param criminalsManager   The criminals manager responsible for managing criminals.
     */
    public EntityBuilder(EntityManager entityManager, LocationsManager locationsManager,
                         CriminalsManager criminalsManager) {
        this.entityManager = entityManager;
        this.locationsManager = locationsManager;
        this.criminalsManager = criminalsManager;
    }

    /**
     * Creates and returns a new entity with the specified ID, current location ID, and entity type.
     *
     * @param ID                The unique identifier for the new entity.
     * @param currLocationID   The current location ID where the entity is initially located.
     * @param type              The type of the entity to be created (Pirate, Trader, Police).
     * @return                  The newly created entity.
     * @throws RuntimeException if an incorrect EntityType is received.
     */
    public Entity newEntity(int ID, int currLocationID, EntityType type) {
        var shipType = ShipType.valueOf(Constants.builders.getString(defaultShipKey + type));
        var ship = ShipBuilder.buildShip(shipType);

        var goods = new int[9];
        var credits = Constants.builders.getLong(startCreditsKey + type).intValue();
        var stats = new EntityStats(credits, goods);
        var travelManager = new TravelManager(locationsManager, currLocationID, ID);
        var prevAction = EntityActions.none;

        return switch (type) {
            case Pirate -> new Pirate(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID);
            case Trader -> new Trader(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID);
            case Police -> new Police(travelManager, entityManager, criminalsManager,
                    ship, stats, prevAction, ID, currLocationID);
            default -> throw new RuntimeException("Incorrect EntityType received!");
        };
    }

    /**
     * Creates and returns a new player entity with the specified ID, current location ID, and name.
     *
     * @param ID                The unique identifier for the new player entity.
     * @param currLocationID   The current location ID where the player is initially located.
     * @param name              The name of the player entity.
     * @return                  The newly created player entity.
     */
    public Player newPlayer(int ID, int currLocationID, String name) {
        var shipType = ShipType.valueOf(Constants.builders.getString(defaultShipKey + "Player"));
        var ship = ShipBuilder.buildShip(shipType);

        var goods = new int[9];
        var credits = Constants.builders.getLong(startCreditsKey + "Player").intValue();
        var stats = new EntityStats(credits, goods);
        var travelManager = new TravelManager(locationsManager, currLocationID, ID);
        var prevAction = EntityActions.none;

        return new Player(travelManager, ship, stats, name);
    }
}

