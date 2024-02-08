package cz.cuni.mff.pijalekj.ships;

import cz.cuni.mff.pijalekj.enums.ShipSize;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.utils.Constants;

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

    void refuel(int capacity) {
        if (capacity < this.shipStats.fuel.getCurr()) {
            throw new IllegalArgumentException("Ship has more fuel than what it was given.");
        }
        this.shipStats.fuel.setCurr(capacity);
    }

    void refuel() {
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
        shipStats.health.setCurr(newHealth);
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

    public ShipStats getShipStats() {
        return shipStats;
    }

    public static int damageOutput(Ship attacker, Ship defender) {
        double att_damage = attacker.shipStats.damage;
        double def_maneuver = defender.shipStats.maneuver;

        return Math.toIntExact(
                Math.round(att_damage * (1 - (def_maneuver / (100 + Constants.BATTLE_COEFF)))));
    }

}
