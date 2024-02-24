package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.battle.Playerlike;
import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;

/**
 * The abstract Entity class, representing a game entity with common properties and actions.
 * It implements the Playerlike interface.
 */
public abstract class Entity implements Playerlike {

    /**
     * The TravelManager responsible for managing travel-related functionalities.
     */
    protected final TravelManager travelManager;

    /**
     * The EntityManager for getting information about Entities in the game.
     */
    protected final EntityManager entityManager;

    /**
     * The Ship owned by the entity.
     */
    protected Ship ownedShip;

    /**
     * The statistics of the entity.
     */
    protected EntityStats entityStats;

    /**
     * The previous action performed by the entity.
     */
    protected EntityActions prevAction;

    /**
     * The unique identifier for the entity.
     */
    protected final int entityID;

    /**
     * Gets the Ship owned by the entity.
     *
     * @return The owned Ship.
     */
    public Ship getOwnedShip() {
        return ownedShip;
    }

    /**
     * Sets the Ship owned by the entity.
     *
     * @param ownedShip The Ship to be set as owned.
     */
    public void setOwnedShip(Ship ownedShip) {
        this.ownedShip = ownedShip;
    }

    /**
     * Gets the statistics of the entity.
     *
     * @return The EntityStats of the entity.
     */
    public EntityStats getEntityStats() {
        return entityStats;
    }

    /**
     * Sets the EntityStats for the entity.
     *
     * @param entityStats The EntityStats to be set.
     */
    public void setEntityStats(EntityStats entityStats) {
        this.entityStats = entityStats;
    }

    /**
     * Constructs an Entity with specified attributes.
     *
     * @param travelManager The TravelManager for managing travel-related functionalities.
     * @param entityManager The EntityManager for getting information about other Entities within the game.
     * @param ownedShip The Ship owned by the entity.
     * @param entityStats The statistics of the entity.
     * @param prevAction The previous action performed by the entity.
     * @param entityID The unique identifier for the entity.
     */
    protected Entity(TravelManager travelManager, EntityManager entityManager,
                     Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        this.travelManager = travelManager;
        this.entityManager = entityManager;
        this.ownedShip = ownedShip;
        this.entityStats = entityStats;
        this.prevAction = prevAction;
        this.entityID = entityID;
    }

    /**
     * Abstract method representing the entity's action during a turn.
     *
     * @return An OptionalInt representing the entity's chosen action outcome.
     */
    public abstract OptionalInt play();

    /**
     * Abstract method representing the entity's decision in battle against an opponent.
     *
     * @param opponent The opponent entity.
     * @return A BattleDecision indicating the entity's battle decision.
     */
    public abstract BattleDecision battle(Playerlike opponent);

    /**
     * Abstract method representing the entity's actions when it wins a battle against an opponent.
     *
     * @param opponent The opponent entity.
     */
    public abstract void won(Playerlike opponent);

    /**
     * Inflicts damage on the entity's owned ship.
     *
     * @param damage The amount of damage to be inflicted.
     */
    public void takeDamage(int damage) {
        ownedShip.takeDamage(damage);
    }

    /**
     * Checks if the entity's owned ship is alive.
     *
     * @return true if the ship is alive, false otherwise.
     */
    public boolean isAlive() {
        return ownedShip.isAlive();
    }

    /**
     * Checks if the entity's cargo is full.
     *
     * @return true if the cargo is full, false otherwise.
     */
    public boolean isFull() {
        return entityStats.getTotalGoodsAmount() != 0;
    }

    /**
     * Gets the current position of the entity.
     *
     * @return The current location ID.
     */
    public int getCurrPosition() {
        return travelManager.getCurrLocationID();
    }

    /**
     * Gets the next position the entity plans to travel to.
     *
     * @return The next location ID.
     */
    public int getNextPosition() {
        return travelManager.getNextLocationID();
    }

    /**
     * Gets the unique identifier of the entity.
     *
     * @return The entity ID.
     */
    public int getID() {
        return entityID;
    }

    /**
     * Sets the health of the entity's owned ship to zero, effectively killing the entity.
     */
    public void kill() {
        ownedShip.getStats().hull.setCurr(0);
    }

    /**
     * Checks if the entity is currently traveling.
     *
     * @return true if the entity is traveling, false otherwise.
     */
    public boolean isTraveling() {
        return travelManager.isTraveling();
    }

    /**
     * Transfers all goods from an opponent entity to the entity, considering cargo capacity.
     *
     * @param opponent The opponent entity.
     */
    protected void takeAll(EntityStats opponent) {
        int maxCapacity = ownedShip.getStats().maxCargo;
        int free = maxCapacity - entityStats.getTotalGoodsAmount();

        entityStats.transferAllGoods(opponent, free);
    }

    /**
     * Initiates a travel action, burning fuel and updating travel status.
     */
    protected void travel() {
        ownedShip.burnFuel();
        travelManager.travel();
    }

    /**
     * Calculates and returns the sustainability difference between the entity and a victim ship.
     *
     * @param victimShip The victim ship in a battle scenario.
     * @return The sustainability difference.
     */
    protected int outSustain(Ship victimShip) {
        // Calculation of sustainability difference between the entity and a victim ship
        // based on health, shields, and damage output.
        var myHealth = ownedShip.getStats().hull.getCurr() +
                ownedShip.getStats().shields.getCurr();
        var victimHealth = victimShip.getStats().hull.getCurr() +
                victimShip.getStats().shields.getCurr();

        var attackerDamage = Ship.damageOutput(ownedShip, victimShip);
        var victimDamage = Ship.damageOutput(victimShip, ownedShip);

        // + 1 to prevent division by 0, should not affect the outcome
        var mySustain = myHealth / (victimDamage + 1);
        var victimSustain = victimHealth / (attackerDamage + 1);

        return mySustain - victimSustain;
    }

    /**
     * Performs maintenance actions, including shield recharge, refueling, and hull repair.
     */
    protected void maintenance() {
        // Shields always recharge when at a planet
        ownedShip.rechargeShields();

        // Fuel
        int fuelDiff = ownedShip.getStats().fuel.getMax() - ownedShip.getStats().fuel.getCurr();
        int neededCredits = fuelDiff * Constants.fuelCost;

        if (fuelDiff > 0 && neededCredits <= entityStats.getCredits()) {
            entityStats.removeCredits(neededCredits);
            ownedShip.refuel(fuelDiff);
        } else {
            entityStats.addCredits(50);
        }

        // Hull
        int hullDiff = ownedShip.getStats().hull.getMax() - ownedShip.getStats().hull.getCurr();
        neededCredits = fuelDiff * Constants.fuelCost;
        if (hullDiff > 0 && neededCredits <= entityStats.getCredits()) {
            entityStats.removeCredits(neededCredits);
            ownedShip.repairHull(hullDiff);
        } else {
            entityStats.addCredits(50);
        }
    }

    /**
     * Sells all goods the entity possesses at the current planet and adds credits accordingly.
     */
    protected void sell() {
        var currPlanet = travelManager.getCurrLocation();
        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            entityStats.addCredits(currPlanet.sell(index, entityStats.getGoodAmount(index)));
            entityStats.removeGood(index, entityStats.getGoodAmount(index));
        }
    }
}
