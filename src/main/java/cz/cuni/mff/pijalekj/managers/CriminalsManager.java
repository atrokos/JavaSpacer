package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.constants.Constants;

import java.util.HashMap;

/**
 * The CriminalsManager class extends HashMap and represents a manager for tracking
 * entities marked as criminals. It provides methods to check, add, and remove entities
 * from the list of criminals, as well as updating the remaining timeout for each criminal.
 */
public class CriminalsManager extends HashMap<Integer, Integer> {

    /**
     * Checks if the specified entity is marked as a criminal.
     *
     * @param entityID The unique identifier of the entity.
     * @return True if the entity is a criminal, false otherwise.
     */
    public boolean isCriminal(int entityID) {
        return containsKey(entityID);
    }

    /**
     * Adds a new criminal entity with a default timeout to the manager.
     *
     * @param entityID The unique identifier of the criminal entity.
     */
    public void addCriminal(int entityID) {
        put(entityID, Constants.DEFAULT_TIMEOUT);
    }

    /**
     * Removes the specified entity from the list of criminals.
     *
     * @param entityID The unique identifier of the entity to be removed.
     */
    public void removeCriminal(int entityID) {
        remove(entityID);
    }

    /**
     * Updates the remaining timeout for each criminal entity and removes entities
     * with a timeout equal to or less than zero.
     */
    public void updateCriminals() {
        replaceAll((key, value) -> value - 1);
        entrySet().removeIf(item -> item.getValue() <= 0);
    }
}

