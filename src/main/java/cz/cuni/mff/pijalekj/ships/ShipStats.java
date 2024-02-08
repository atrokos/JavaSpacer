package cz.cuni.mff.pijalekj.ships;

public class ShipStats {
    public MaxValue health;

    /**
     *  Cargo capacity of the ship.
     */
    public MaxValue cargo;

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
    public double fleeChance = 0.;

    public ShipStats(MaxValue health, MaxValue cargo, MaxValue fuel,
                     MaxValue shields, int damage, int maneuver) {
        this.health = health;
        this.cargo = cargo;
        this.fuel = fuel;
        this.shields = shields;
        this.damage = damage;
        this.maneuver = maneuver;
    }
}
