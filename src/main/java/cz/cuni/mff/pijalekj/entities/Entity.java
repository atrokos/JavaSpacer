package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import javax.swing.border.EmptyBorder;

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
        return ownedShip.getShipStats().health.getCurr() > 0;
    }

    public boolean isEmpty() {
        return ownedShip.getShipStats().cargo.getCurr() == 0;
    }

    public void changeGoodsBy(GoodsIndex type, int number) {
        entityStats.ownedGoods[type.ordinal()] += number;
        ownedShip.getShipStats().cargo.changeBy(number);
    }

    public void changeGoodsBy(int type, int number) {
        entityStats.ownedGoods[type] += number;
        ownedShip.getShipStats().cargo.changeBy(number);
    }

    public int getCurrPosition() {
        return travelManager.getCurrPosition();
    }

    public int getNextPosition() {
        return travelManager.getNextPosition();
    }

    public int getID() {
        return entityID;
    }

    public void kill() {
        ownedShip.getShipStats().health.setCurr(0);
    }

    public boolean isTraveling() {
        return travelManager.isTraveling();
    }

    private void takeAll(Entity opponent) {
        if (opponent.isEmpty() || opponent.isAlive()) {
            return;
        }

        int maxCapacity = ownedShip.getShipStats().cargo.getMax();
        var opGoods = opponent.entityStats.ownedGoods;

        for (int i = 0; i < opGoods.length; ++i) {
            int diff = maxCapacity - opGoods[i] - ownedShip.getShipStats().cargo.getCurr();
            if (diff >= 0) {
                changeGoodsBy(i, opGoods[i]);
                opponent.changeGoodsBy(i, -opGoods[i]);
            }
            else {
                changeGoodsBy(i, ownedShip.getShipStats().cargo.getCurr());
                opponent.changeGoodsBy(i, -ownedShip.getShipStats().cargo.getCurr());
            }
        }
    }

    private void travel() {
        ownedShip.getShipStats().fuel.changeBy(-1);
        travelManager.travel();
    }

    private int outSustain(Entity victim) {
        var myHealth = this.ownedShip.getShipStats().health.getCurr() +
                this.ownedShip.getShipStats().shields.getCurr();
        var victimHealth = victim.ownedShip.getShipStats().health.getCurr() +
                victim.ownedShip.getShipStats().shields.getCurr();

        var attackerDamage = Ship.damageOutput(ownedShip, victim.ownedShip);
        var victimDamage = Ship.damageOutput(victim.ownedShip, ownedShip);

        // + 1 to prevent division by 0, should not affect the outcome
        var mySustain = myHealth / (victimDamage + 1);
        var victimSustain = victimHealth / (attackerDamage + 1);

        return mySustain - victimSustain;
    }
}