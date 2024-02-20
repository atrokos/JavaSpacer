package cz.cuni.mff.pijalekj.ships;

import cz.cuni.mff.pijalekj.enums.ShipSize;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.constants.Constants;


public class Ship {
    private final ShipStats shipStats;
    private final ShipSize shipSize;
    private final ShipType shipType;

    public Ship(ShipStats shipStats, ShipSize shipSize, ShipType shipType) {
        this.shipStats = shipStats;
        this.shipSize = shipSize;
        this.shipType = shipType;

        shipStats.fleeChance =
                (0.8 * Math.pow(shipStats.maneuver / 100.0, 2) + 0.2) * 100;
    }

    public void refuel(int capacity) {
        assert capacity + this.shipStats.fuel.getCurr() <= this.shipStats.fuel.getMax():
                "Ship was told to refuel more than it can store.";
        this.shipStats.fuel.changeBy(capacity);
    }

    public void refuel() {
        shipStats.fuel.setToMax();
    }

    public void rechargeShields() {
        shipStats.shields.setToMax();
    }

    public void takeDamage(int damage) {
        int carry_damage = shipStats.shields.getCurr() - damage;
        if (carry_damage < 0)
        {
            shipStats.shields.setCurr(0);
            // Addition, because carry_damage is negative in this case
            shipStats.health.setCurr(shipStats.health.getCurr() + carry_damage);
        }
        else
        {
            shipStats.shields.setCurr(carry_damage);
        }
    }

    public void repairHull(int newHealth){
        assert newHealth + this.shipStats.health.getCurr() <= this.shipStats.health.getMax():
                "Ship was told to repair more than it can be.";

        shipStats.health.changeBy(newHealth);
    }

    public void repairHull() {
        shipStats.health.setToMax();
    }

    public ShipSize getShipSize() {
        return shipSize;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public ShipStats getStats() {
        return shipStats;
    }

    public double getFleeChance() {
        return shipStats.fleeChance;
    }

    public static int damageOutput(Ship attacker, Ship defender) {
        double att_damage = attacker.shipStats.damage;
        double def_maneuver = defender.shipStats.maneuver;

        return Math.toIntExact(
                Math.round(att_damage * (1 - (def_maneuver / (100 + Constants.BATTLE_COEFF)))));
    }

}
