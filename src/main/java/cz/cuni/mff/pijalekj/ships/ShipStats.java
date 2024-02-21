package cz.cuni.mff.pijalekj.ships;

public class ShipStats {
    public MaxValue health;

    /**
     *  Cargo capacity of the ship.
     */
    public final int maxCargo;

    /**
     *  Fuel of the ship.
     */
    public MaxValue fuel;

    /**
     *  Ship's shields.
     */
    public MaxValue shields;

    /**
     *  Damage done by the ship.
     */
    public int damage;

    /**
     *  Maneuver of the ship.
     */
    public int maneuver;

    /**
     *  Ship's chance to flee from combat.
     */
    public int fleeChance = 0;

    public ShipStats(MaxValue health, int cargo, MaxValue fuel,
                     MaxValue shields, int damage, int maneuver) {
        this.health = health;
        this.maxCargo = cargo;
        this.fuel = fuel;
        this.shields = shields;
        this.damage = damage;
        this.maneuver = maneuver;
    }
}
