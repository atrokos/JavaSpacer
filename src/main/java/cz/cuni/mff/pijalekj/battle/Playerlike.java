package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.entities.EntityStats;
import cz.cuni.mff.pijalekj.ships.Ship;

public interface Playerlike {
    Ship getOwnedShip();
    EntityStats getEntityStats();
}
