package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.builders.EntityBuilder;
import cz.cuni.mff.pijalekj.entities.*;
import cz.cuni.mff.pijalekj.enums.EntityType;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.*;

import static cz.cuni.mff.pijalekj.enums.EntityType.Trader;
/**
 * The EntityManager class is responsible for owning and managing all entities in the game.
 * It provides methods to add, remove, and retrieve entities. Player is treated as a separate
 * class with an ID of -1.
 */
public class EntityManager {
    private Entity[] entities;
    private Player player;

    /**
     * Constructs a new EntityManager.
     */
    public EntityManager() {}

    /**
     * Retrieves the ship associated with the specified entity ID.
     * If the entity ID is -1, returns the player's owned ship.
     *
     * @param entityID The ID of the entity.
     * @return The Ship associated with the entity.
     */
    public Ship getEntityShip(int entityID) {
        if (entityID == -1) {
            return player.ownedShip;
        }
        return entities[entityID].getOwnedShip();
    }

    /**
     * Sets the array of entities for this EntityManager.
     *
     * @param entities The array of entities to be set.
     */
    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }
    /**
     * Retrieves the entity with the specified ID.
     *
     * @param entityID The ID of the entity to be retrieved.
     * @return The Entity with the specified ID.
     */
    public Entity getEntity(int entityID) {
        return entities[entityID];
    }

    /**
     * Retrieves all entities managed by this EntityManager.
     *
     * @return An array of entities.
     */
    public Entity[] getEntities() {
        return entities;
    }

    /**
     * Retrieves the Player class associated with this EntityManager.
     *
     * @return The associated Player class.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the Player to be used in this EntityManager.
     *
     * @param player The Player to be set.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    /**
     * Sets the Player to be used in this EntityManager.
     *
     * @param name The Player to be set.
     */
    public void setPlayerName(String name) {
        player.setName(name);
    }

    /**
     * This method lets entities play and collects their targets (if any).
     * Everyone can have at most 1 attacker.
     *
     * @param playerAttack An optional player attack, if present.
     * @return A HashMap containing entity IDs as keys and their respective targets as values.
     */
    public HashMap<Integer, Integer> play(OptionalInt playerAttack) {
        var results = new HashMap<Integer, Integer>();
        var victims = new HashSet<Integer>();
        if (playerAttack.isPresent()) {
            results.put(-1, playerAttack.getAsInt());
            victims.add(playerAttack.getAsInt());
        }

        for (int i = 0; i < entities.length; i++) {
            OptionalInt result = entities[i].play();
            if (result.isEmpty()) {
                continue;
            }

            if (!results.containsKey(result.getAsInt()) && !victims.contains(result.getAsInt())) {
                results.put(i, result.getAsInt());
                victims.add(result.getAsInt());
            }
        }

        return results;
    }

    /**
     * Resets non-player entities by creating new entities of the same type if they are not alive.
     *
     * @param criminalsManager The manager for criminals that the new Entities will be associated with.
     * @param locationsManager The manager for locations that the new Entities will be associated with.
     */
    public void resetNPCs(CriminalsManager criminalsManager, LocationsManager locationsManager) {
        EntityBuilder eb = new EntityBuilder(this, locationsManager, criminalsManager);
        for (var entity : entities) {
            if (entity == null) {
                throw new RuntimeException("Fatal error: an Entity is null!");
            }

            if (!entity.isAlive()) {
                locationsManager.removeEntityFrom(entity.getID(), entity.getCurrPosition());
                switch (entity) {
                    case Police police -> entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), EntityType.Police);
                    case Pirate pirate -> entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), EntityType.Pirate);
                    case Trader trader -> entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), Trader);
                    default -> throw new IllegalStateException("Unexpected value: " + entity);
                }
            }
            locationsManager.addEntityTo(entity.getID(), entity.getCurrPosition());
        }
    }
}
