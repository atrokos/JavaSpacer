package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.Entity;

public class EntityManager {
    private final Entity[] entities;

    public EntityManager(Entity[] entities) {
        this.entities = entities;
    }

    public Entity getEntity(int entityID) {
        return entities[entityID];
    }

    public Entity[] getEntities() {
        return entities;
    }

    public boolean play() {
        entities[0].play(); // Let the player play

        if (!entities[0].isAlive()) { // If the player is dead, end the game
            return false;
        }

        for (int i = 1; i < entities.length; ++i) { // Let the NPCs play
            entities[i].play();
        }

        return true;
    }

    public void resetNPCs(CriminalsManager criminalsManager, LocationsManager locationsManager) {
        // TODO
    }
}
