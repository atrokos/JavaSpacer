package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.builders.EntityBuilder;
import cz.cuni.mff.pijalekj.entities.*;
import cz.cuni.mff.pijalekj.enums.EntityType;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.*;

import static cz.cuni.mff.pijalekj.enums.EntityType.Trader;

public class EntityManager {
    private Entity[] entities;
    private Player player;

    public EntityManager() {}

    public Ship getEntityShip(int entityID) {
        if (entityID == -1) {
            return this.player.ownedShip;
        }
        return this.entities[entityID].getOwnedShip();
    }
    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }
    public Entity getEntity(int entityID) {
        return this.entities[entityID];
    }

    public Entity[] getEntities() {
        return this.entities;
    }

    public Player getPlayer() {
        return this.player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setPlayerName(String name) {
        this.player.setName(name);
    }

    /// This method lets Entities play and collects their targets (if any).
    /// Everyone can have at most 1 attacker.
    public HashMap<Integer, Integer> play(OptionalInt playerAttack) {
        var results = new HashMap<Integer, Integer>();
        var victims = new HashSet<Integer>();
        if (playerAttack.isPresent()) {
            results.put(-1, playerAttack.getAsInt());
            victims.add(playerAttack.getAsInt());
        }

        for (int i = 0; i < this.entities.length; i++) {
            OptionalInt result = this.entities[i].play();
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


    public void resetNPCs(CriminalsManager criminalsManager, LocationsManager locationsManager) {
        EntityBuilder eb = new EntityBuilder(this, locationsManager, criminalsManager);
        for (var entity : this.entities) {
            if (entity == null) {
                throw new RuntimeException("Fatal error: an Entity is null!");
            }

            if (!entity.isAlive()) {
                locationsManager.removeEntityFrom(entity.getID(), entity.getCurrPosition());
                switch (entity) {
                    case Police police -> {
                        entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), EntityType.Police);
                    }
                    case Pirate pirate -> {
                        entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), EntityType.Pirate);
                    }
                    case Trader trader -> {
                        entity = eb.newEntity(entity.getID(), entity.getCurrPosition(), Trader);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + entity);
                }
            }
            locationsManager.addEntityTo(entity.getID(), entity.getCurrPosition());
        }
    }
}
