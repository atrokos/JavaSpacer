package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.builders.EntityBuilder;
import cz.cuni.mff.pijalekj.entities.*;
import cz.cuni.mff.pijalekj.enums.EntityType;

import java.util.ArrayList;

import static cz.cuni.mff.pijalekj.enums.EntityType.Trader;

public class EntityManager {
    private final ArrayList<Entity> entities = new ArrayList<>();

    public EntityManager() {}

    public Entity getEntity(int entityID) {
        return entities.get(entityID);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return (Player) entities.getFirst();
    }

    public boolean play() {
        entities.getFirst().play(); // Let the player play

        if (!entities.getFirst().isAlive()) { // If the player is dead, end the game
            return false;
        }

        for (int i = 1; i < entities.size(); ++i) { // Let the NPCs play
            entities.get(i).play();
        }

        return true;
    }

    public void resetNPCs(CriminalsManager criminalsManager, LocationsManager locationsManager) {
        EntityBuilder eb = new EntityBuilder(this, locationsManager, criminalsManager);
        for (var entity : entities) {
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

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
}
