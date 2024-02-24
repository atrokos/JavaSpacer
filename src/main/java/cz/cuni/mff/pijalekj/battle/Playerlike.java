package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.entities.EntityStats;
import cz.cuni.mff.pijalekj.ships.Ship;

/**
 * The Playerlike interface represents entities that share common characteristics with the player
 * in the game. Implementing classes are expected to provide methods to retrieve the owned ship
 * and entity statistics.
 */
public interface Playerlike {
    /**
     * Retrieves the ship owned by the entity.
     *
     * @return The Ship object representing the owned ship.
     */
    Ship getOwnedShip();

    /**
     * Retrieves the entity statistics, including credits and goods owned.
     *
     * @return The EntityStats object representing the entity's statistics.
     */
    EntityStats getEntityStats();
}
