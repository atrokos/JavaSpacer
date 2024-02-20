package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

public abstract class Entity implements BattleReady {
    protected TravelManager travelManager;
    protected EntityManager entityManager;
    protected CriminalsManager criminalsManager;

    protected Ship ownedShip;
    protected EntityStats entityStats;
    protected EntityActions prevAction;
    protected final int entityID;

    public Ship getOwnedShip() {
        return ownedShip;
    }

    public void setOwnedShip(Ship ownedShip) {
        this.ownedShip = ownedShip;
    }

    public EntityStats getEntityStats() {
        return entityStats;
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

    public abstract void play();

    public boolean isAlive() {
        return ownedShip.getStats().health.getCurr() > 0;
    }

    public boolean isEmpty() {
        return entityStats.getTotalGoodsAmount() == 0;
    }

    public int getCurrPosition() {
        return travelManager.getCurrLocationID();
    }

    public int getNextPosition() {
        return travelManager.getNextLocationID();
    }

    public int getID() {
        return entityID;
    }

    public void kill() {
        ownedShip.getStats().health.setCurr(0);
    }

    public boolean isTraveling() {
        return travelManager.isTraveling();
    }

    protected void takeAll(Entity opponent) {
        if (opponent.isEmpty() || opponent.isAlive()) {
            return;
        }

        int maxCapacity = ownedShip.getStats().maxCargo;
        int free = maxCapacity - entityStats.getTotalGoodsAmount();

        this.entityStats.transferAllGoods(opponent.entityStats, free);
    }

    protected void travel() {
        ownedShip.getStats().fuel.changeBy(-1);
        travelManager.travel();
    }

    protected int outSustain(Entity victim) {
        var myHealth = this.ownedShip.getStats().health.getCurr() +
                this.ownedShip.getStats().shields.getCurr();
        var victimHealth = victim.ownedShip.getStats().health.getCurr() +
                victim.ownedShip.getStats().shields.getCurr();

        var attackerDamage = Ship.damageOutput(ownedShip, victim.ownedShip);
        var victimDamage = Ship.damageOutput(victim.ownedShip, ownedShip);

        // + 1 to prevent division by 0, should not affect the outcome
        var mySustain = myHealth / (victimDamage + 1);
        var victimSustain = victimHealth / (attackerDamage + 1);

        return mySustain - victimSustain;
    }

    protected void maintenance() {
        // Shields always recharge when at a planet
        ownedShip.rechargeShields();

        // Fuel
        int fuelDiff = ownedShip.getStats().fuel.getMax() - ownedShip.getStats().fuel.getCurr();
        int neededCredits = fuelDiff * Constants.fuelCost;

        if (fuelDiff > 0 && neededCredits <= entityStats.getCredits()) {
            entityStats.removeCredits(neededCredits);
            ownedShip.refuel(fuelDiff);
        }
        else {
            entityStats.addCredits(50);
        }

        // Hull
        int hullDiff = ownedShip.getStats().health.getMax() - ownedShip.getStats().health.getCurr();
        neededCredits = fuelDiff * Constants.fuelCost;
        if (hullDiff > 0 && neededCredits <= entityStats.getCredits()) {
            entityStats.removeCredits(neededCredits);
            ownedShip.repairHull(hullDiff);
        }
        else {
            entityStats.addCredits(50);
        }
    }

    protected void sell() {
        var currPlanet = travelManager.getCurrLocation();
        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            this.entityStats.addCredits(currPlanet.sell(index, this.entityStats.getGoodAmount(index)));
            this.entityStats.removeGood(index, this.entityStats.getGoodAmount(index));
        }
    }
}