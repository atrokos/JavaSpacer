package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;

public abstract class Entity {
    protected TravelManager travelManager;
    protected EntityManager entityManager;
    protected CriminalsManager criminalsManager;

    protected Ship ownedShip;
    protected EntityStats entityStats;
    protected EntityActions prevAction;
    protected final int entityID;

    public Ship getOwnedShip() {
        return this.ownedShip;
    }

    public void setOwnedShip(Ship ownedShip) {
        this.ownedShip = ownedShip;
    }

    public EntityStats getEntityStats() {
        return this.entityStats;
    }

    public void setEntityStats(EntityStats entityStats) {
        this.entityStats = entityStats;
    }

    public Entity(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        this.travelManager = travelManager;
        this.entityManager = entityManager;
        this.criminalsManager = criminalsManager;
        this.ownedShip = ownedShip;
        this.entityStats = entityStats;
        this.prevAction = prevAction;
        this.entityID = entityID;
    }

    public abstract OptionalInt play();
    public abstract BattleDecision battle(Entity opponent);
    public abstract void won(Entity opponent);
    public abstract void lost();
    public void takeDamage(int damage) {
        this.ownedShip.takeDamage(damage);
    }

    public boolean isAlive() {
        return this.ownedShip.isAlive();
    }

    public boolean isFull() {
        return this.entityStats.getTotalGoodsAmount() != 0;
    }

    public int getCurrPosition() {
        return this.travelManager.getCurrLocationID();
    }

    public int getNextPosition() {
        return this.travelManager.getNextLocationID();
    }

    public int getID() {
        return this.entityID;
    }

    public void kill() {
        this.ownedShip.getStats().health.setCurr(0);
    }

    public boolean isTraveling() {
        return this.travelManager.isTraveling();
    }

    protected void takeAll(EntityStats opponent) {
        int maxCapacity = this.ownedShip.getStats().maxCargo;
        int free = maxCapacity - this.entityStats.getTotalGoodsAmount();

        this.entityStats.transferAllGoods(opponent, free);
    }

    protected void travel() {
        this.ownedShip.getStats().fuel.changeBy(-1);
        this.travelManager.travel();
    }

    protected int outSustain(Ship victimShip) {
        var myHealth = this.ownedShip.getStats().health.getCurr() +
                this.ownedShip.getStats().shields.getCurr();
        var victimHealth = victimShip.getStats().health.getCurr() +
                victimShip.getStats().shields.getCurr();

        var attackerDamage = Ship.damageOutput(this.ownedShip, victimShip);
        var victimDamage = Ship.damageOutput(victimShip, this.ownedShip);

        // + 1 to prevent division by 0, should not affect the outcome
        var mySustain = myHealth / (victimDamage + 1);
        var victimSustain = victimHealth / (attackerDamage + 1);

        return mySustain - victimSustain;
    }

    protected void maintenance() {
        // Shields always recharge when at a planet
        this.ownedShip.rechargeShields();

        // Fuel
        int fuelDiff = this.ownedShip.getStats().fuel.getMax() - this.ownedShip.getStats().fuel.getCurr();
        int neededCredits = fuelDiff * Constants.fuelCost;

        if (fuelDiff > 0 && neededCredits <= this.entityStats.getCredits()) {
            this.entityStats.removeCredits(neededCredits);
            this.ownedShip.refuel(fuelDiff);
        }
        else {
            this.entityStats.addCredits(50);
        }

        // Hull
        int hullDiff = this.ownedShip.getStats().health.getMax() - this.ownedShip.getStats().health.getCurr();
        neededCredits = fuelDiff * Constants.fuelCost;
        if (hullDiff > 0 && neededCredits <= this.entityStats.getCredits()) {
            this.entityStats.removeCredits(neededCredits);
            this.ownedShip.repairHull(hullDiff);
        }
        else {
            this.entityStats.addCredits(50);
        }
    }

    protected void sell() {
        var currPlanet = this.travelManager.getCurrLocation();
        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            this.entityStats.addCredits(currPlanet.sell(index, this.entityStats.getGoodAmount(index)));
            this.entityStats.removeGood(index, this.entityStats.getGoodAmount(index));
        }
    }
}