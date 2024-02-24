package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.battle.Playerlike;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;
import java.util.Random;

/**
 * The Police class represents a police entity in the game, extending the Entity class.
 * It includes additional functionality for enforcing law and maintaining order.
 */
public class Police extends Entity {
    private final int homePlanetID;
    private final CriminalsManager criminalsManager;

    /**
     * Constructs a Police object with specified attributes.
     *
     * @param travelManager     The TravelManager for managing travel-related functionalities.
     * @param entityManager     The EntityManager for managing entities in the game.
     * @param criminalsManager  The CriminalsManager for managing criminal entities in the game.
     * @param ownedShip         The Ship owned by the police.
     * @param entityStats       The EntityStats representing the police's statistics and inventory.
     * @param prevAction        The previous action of the police.
     * @param entityID          The ID of the police entity.
     * @param homePlanetID      The ID of the home planet where the police is stationed.
     */
    public Police(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID, int homePlanetID)
    {
        super(travelManager, entityManager, ownedShip, entityStats, prevAction, entityID);
        this.homePlanetID = homePlanetID;
        this.criminalsManager = criminalsManager;
    }

    /**
     * Handles the battle decision for the police. The police always choose to attack.
     *
     * @param opponent The player or another entity to battle.
     * @return A BattleDecision representing the police's decision to attack.
     */
    @Override
    public BattleDecision battle(Playerlike opponent) {
        return new BattleDecision(BattleActionType.attack, Ship.damageOutput(ownedShip, opponent.getOwnedShip()));
    }

    /**
     * Handles the actions to be performed when the police wins a battle.
     *
     * @param opponent The player or another entity that the police defeated.
     */
    @Override
    public void won(Playerlike opponent) {
        takeAll(opponent.getEntityStats());
    }

    /**
     * Handles the police's turn during gameplay, determining actions such as scanning and traveling.
     *
     * @return An OptionalInt representing the ID of the criminal entity to interact with.
     */
    @Override
    public OptionalInt play() {
        if (!isAlive()) {
            return OptionalInt.empty();
        }

        // This NPC is travelling
        if (travelManager.isTraveling()) {
            // If the police have scanned or fought someone during this travel, do nothing for the rest
            if (prevAction == EntityActions.battle || prevAction == EntityActions.scan) {
                travel();
                return OptionalInt.empty();
            }

            return findCriminal();
        }

        // This NPC is at a planet. Do maintenance and sell all confiscated goods (if any).
        prevAction = EntityActions.maintenance;
        maintenance();
        if (isFull()) {
            sell();
        }

        // If the Police are not at their stationed planet, travel back to it
        if (travelManager.getCurrLocationID() != homePlanetID) {
            travelManager.travelStart(homePlanetID);
        } else {
            // Otherwise, travel to a random neighbor
            var neighbors = travelManager.getNeighbors();
            int randomIndex = new Random().nextInt(neighbors.length);

            prevAction = EntityActions.travelPrep;
            travelManager.travelStart(neighbors[randomIndex]);
        }

        return OptionalInt.empty();
    }

    /**
     * Finds a criminal entity present in the same location as the police.
     *
     * @return An OptionalInt representing the ID of the criminal entity, if found.
     */
    private OptionalInt findCriminal() {
        return travelManager.getPresentEntities().stream().mapToInt(i -> i)
                .filter(ID -> criminalsManager.isCriminal(ID) && ID != entityID)
                .findFirst();
    }

    /**
     * Handles maintenance tasks specific to the police, including shield recharge, hull repair, and refueling.
     */
    @Override
    protected void maintenance() {
        // Shields always recharge when at a planet
        ownedShip.rechargeShields();
        ownedShip.repairHull();
        ownedShip.refuel();
    }

    /**
     * Retrieves the home planet ID where the police is stationed.
     *
     * @return The ID of the home planet.
     */
    private int getHomePlanetID() {
        return homePlanetID;
    }
}

