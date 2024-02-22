package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;
import java.util.Random;

public class Police extends Entity {
    private final int homePlanetID;

    public Police(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID, int homePlanetID)
    {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);
        this.homePlanetID = homePlanetID;
    }

    @Override
    public BattleDecision battle(Entity opponent) {
        return new BattleDecision(BattleActionType.attack, Ship.damageOutput(this.ownedShip, opponent.getOwnedShip()));
    }

    @Override
    public void won(Entity opponent) {
        this.takeAll(opponent.entityStats);
    }

    @Override
    public void lost() {
    }

    @Override
    public OptionalInt play() {
        System.out.print("Police");
        if (!this.isAlive()) {
            return OptionalInt.empty();
        }

        // This NPC is travelling
        if (this.travelManager.isTraveling()) {
            // If the police have scanned or fought someone during this travel, do nothing for the rest
            if (this.prevAction == EntityActions.battle || this.prevAction == EntityActions.scan) {
                this.travel();
                return OptionalInt.empty();
            }

            return this.findCriminal();
        }

        // This NPC is at a planet. Do maintenance and sell all confiscated goods (if any).
        this.prevAction = EntityActions.maintenance;
        this.maintenance();
        if (this.isFull()) {
            this.sell();
        }

        // Otherwise, travel to a random neighbor
        var neighbors = this.travelManager.getNeighbors();
        int randomIndex = new Random().nextInt(neighbors.length);

        this.prevAction = EntityActions.travelPrep;
        this.travelManager.travelStart(neighbors[randomIndex]);
        return OptionalInt.empty();
    }

    private OptionalInt findCriminal() {
        return this.travelManager.getPresentEntities().stream().mapToInt(i -> i)
                .filter(ID -> this.criminalsManager.isCriminal(ID) && ID != this.entityID)
                .findFirst();
    }

    @Override
    protected void maintenance() {
        // Shields always recharge when at a planet
        this.ownedShip.rechargeShields();
        this.ownedShip.repairHull();
        this.ownedShip.refuel();
    }

    private int getHomePlanetID() {
        return this.homePlanetID;
    }
}
