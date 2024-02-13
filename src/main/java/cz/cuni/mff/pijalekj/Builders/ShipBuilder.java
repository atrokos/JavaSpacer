package cz.cuni.mff.pijalekj.Builders;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.ShipSize;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.ships.MaxValue;
import cz.cuni.mff.pijalekj.ships.Ship;
import cz.cuni.mff.pijalekj.ships.ShipStats;

public class ShipBuilder {
    public static Ship buildShip(ShipType type) {
        var stats = buildStats(type);
        var size = getShipSize(type);

        return new Ship(stats, size, type);
    }

    private static ShipStats buildStats(ShipType type) {
        String baseKey = type.toString() + ".";
        var health = new MaxValue(Constants.ships.getLong(baseKey + "Health").intValue());
        var cargo = new MaxValue(0, Constants.ships.getLong(baseKey + "Cargo").intValue());
        var fuel = new MaxValue( 3 * Constants.ships.getLong(baseKey + "MaxJump").intValue());
        var shields = new MaxValue(Constants.ships.getLong(baseKey + "Shields").intValue());
        int damage = Constants.ships.getLong(baseKey + "Damage").intValue();
        int maneuver = Constants.ships.getLong(baseKey + "Maneuver").intValue();

        return new ShipStats(health, cargo, fuel, shields, damage, maneuver);
    }

    private static ShipSize getShipSize(ShipType type) {
        String key = type.toString() + ".Size";
        return ShipSize.valueOf(Constants.ships.getString(key));
    }
}
