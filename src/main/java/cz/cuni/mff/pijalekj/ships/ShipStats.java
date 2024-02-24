package cz.cuni.mff.pijalekj.ships;

public class ShipStats {
    public final MaxValue hull;

    /**
     *  Cargo capacity of the ship.
     */
    public final int maxCargo;

    /**
     *  Fuel of the ship.
     */
    public final MaxValue fuel;

    /**
     *  Ship's shields.
     */
    public final MaxValue shields;

    /**
     *  Damage done by the ship.
     */
    public final int damage;

    /**
     *  Maneuver of the ship.
     */
    public final int maneuver;

    /**
     *  Ship's chance to flee from combat.
     */
    public int fleeChance = 0;

    public ShipStats(MaxValue hull, int cargo, MaxValue fuel,
                     MaxValue shields, int damage, int maneuver) {
        this.hull = hull;
        maxCargo = cargo;
        this.fuel = fuel;
        this.shields = shields;
        this.damage = damage;
        this.maneuver = maneuver;
    }
}
