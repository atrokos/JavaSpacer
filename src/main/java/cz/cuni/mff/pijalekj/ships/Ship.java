package cz.cuni.mff.pijalekj.ships;

import cz.cuni.mff.pijalekj.enums.ShipSize;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.constants.Constants;


/**
 * The Ship class represents a spaceship with various attributes such as stats, size, and type.
 * It provides methods for performing actions like refueling, destruction, repairing, taking damage, and more.
 */
public class Ship {
    private final ShipStats shipStats;
    private final ShipSize shipSize;
    private final ShipType shipType;

    /**
     * Constructs a Ship with the specified ShipStats, ShipSize, and ShipType.
     * Calculates and sets the flee chance based on maneuverability.
     *
     * @param shipStats The statistics of the ship, including damage, maneuverability, shields, hull, and fuel.
     * @param shipSize  The size category of the ship.
     * @param shipType  The type of the ship.
     */
    public Ship(ShipStats shipStats, ShipSize shipSize, ShipType shipType) {
        this.shipStats = shipStats;
        this.shipSize = shipSize;
        this.shipType = shipType;
        double fleeChanceDouble = (0.8 * Math.pow(shipStats.maneuver / 100.0, 2) + 0.2) * 100;
        shipStats.fleeChance = (int) fleeChanceDouble;
    }

    /**
     * Refuels the ship by the specified capacity.
     *
     * @param capacity The amount of fuel to refuel.
     * @throws AssertionError if the total fuel after refueling exceeds the maximum fuel capacity.
     */
    public void refuel(int capacity) {
        assert capacity + shipStats.fuel.getCurr() <= shipStats.fuel.getMax():
                "Ship was told to refuel more than it can store.";
        shipStats.fuel.setCurr(capacity);
    }

    /**
     * Destroys the ship by setting its hull health to zero.
     */
    public void destroy() {
        shipStats.hull.setCurr(0);
    }

    /**
     * Refuels the ship to its maximum fuel capacity.
     */
    public void refuel() {
        shipStats.fuel.setToMax();
    }

    /**
     * Recharges the ship's shields to their maximum value.
     */
    public void rechargeShields() {
        shipStats.shields.setToMax();
    }

    /**
     * Inflicts damage on the ship, considering both shields and hull health.
     *
     * @param damage The amount of damage to inflict.
     */
    public void takeDamage(int damage) {
        int carryDamage = shipStats.shields.getCurr() - damage;
        if (carryDamage < 0) {
            shipStats.shields.setCurr(0);
            shipStats.hull.setCurr(shipStats.hull.getCurr() + carryDamage);
        } else {
            shipStats.shields.setCurr(carryDamage);
        }
    }

    /**
     * Repairs the ship's hull by the specified amount.
     *
     * @param newHealth The amount of health to restore.
     * @throws AssertionError if the total health after repair exceeds the maximum hull health.
     */
    public void repairHull(int newHealth){
        assert newHealth + shipStats.hull.getCurr() <= shipStats.hull.getMax():
                "Ship was told to repair more than it can be.";
        shipStats.hull.setCurr(newHealth);
    }

    /**
     * Repairs the ship's hull to its maximum health.
     */
    public void repairHull() {
        shipStats.hull.setToMax();
    }

    /**
     * Gets the size category of the ship.
     *
     * @return The ShipSize representing the size category of the ship.
     */
    public ShipSize getShipSize() {
        return shipSize;
    }

    /**
     * Gets the type of the ship.
     *
     * @return The ShipType representing the type of the ship.
     */
    public ShipType getShipType() {
        return shipType;
    }

    /**
     * Gets the ShipStats object containing various statistics of the ship.
     *
     * @return The ShipStats object representing the ship's statistics.
     */
    public ShipStats getStats() {
        return shipStats;
    }

    /**
     * Gets the flee chance of the ship.
     *
     * @return The flee chance as an integer percentage.
     */
    public int getFleeChance() {
        return shipStats.fleeChance;
    }

    /**
     * Calculates the damage output of an attacking ship against a defending ship.
     *
     * @param attacker The attacking ship.
     * @param defender The defending ship.
     * @return The calculated damage output after considering maneuverability and battle coefficient.
     */
    public static int damageOutput(Ship attacker, Ship defender) {
        double attDamage = attacker.shipStats.damage;
        double defManeuver = defender.shipStats.maneuver;

        return Math.toIntExact(
                Math.round(attDamage * (1 - (defManeuver / (100 + Constants.BATTLE_COEFF)))));
    }

    /**
     * Checks if the ship is still alive based on its current hull health.
     *
     * @return true if the ship's hull health is greater than zero; false otherwise.
     */
    public boolean isAlive() {
        return shipStats.hull.getCurr() > 0;
    }

    /**
     * Consumes fuel, reducing the current fuel level by one unit.
     */
    public void burnFuel() {
        shipStats.fuel.changeBy(-1);
    }
}

