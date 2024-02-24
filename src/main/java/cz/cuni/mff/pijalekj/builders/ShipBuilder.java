package cz.cuni.mff.pijalekj.builders;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.ShipSize;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.ships.MaxValue;
import cz.cuni.mff.pijalekj.ships.Ship;
import cz.cuni.mff.pijalekj.ships.ShipStats;

/**
 * The ShipBuilder class provides static methods for constructing ship objects,
 * including a method to build ships based on ship types.
 */
public class ShipBuilder {
    /**
     * Builds a ship with the specified ship type.
     *
     * @param type  The ship type.
     * @return      The constructed Ship object.
     */
    public static Ship buildShip(ShipType type) {
        var stats = buildStats(type);
        var size = getShipSize(type);

        return new Ship(stats, size, type);
    }

    /**
     * Builds ship statistics based on the specified ship type.
     *
     * @param type  The ship type.
     * @return      The constructed ShipStats object.
     */
    private static ShipStats buildStats(ShipType type) {
        String baseKey = "Ships." + type + ".";
        var health = new MaxValue(Constants.ships.getLong(baseKey + "Hull").intValue());
        var cargo = Constants.ships.getLong(baseKey + "Cargo").intValue();
        var fuel = new MaxValue(3 * Constants.ships.getLong(baseKey + "MaxJump").intValue());
        var shields = new MaxValue(Constants.ships.getLong(baseKey + "Shields").intValue());
        int damage = Constants.ships.getLong(baseKey + "Damage").intValue();
        int maneuver = Constants.ships.getLong(baseKey + "Maneuver").intValue();

        return new ShipStats(health, cargo, fuel, shields, damage, maneuver);
    }

    /**
     * Retrieves the ship size based on the specified ship type.
     *
     * @param type  The ship type.
     * @return      The ShipSize enum representing the ship size.
     */
    private static ShipSize getShipSize(ShipType type) {
        String key = "Ships." + type + ".Size";
        return ShipSize.valueOf(Constants.ships.getString(key));
    }
}

