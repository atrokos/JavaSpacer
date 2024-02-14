package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.managers.Battle;
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
    public BattleActionType battle(Entity opponent) {
        return BattleActionType.attack;
    }

    @Override
    public void won(Entity opponent) {
        takeAll(opponent);
    }

    @Override
    public void lost() {
        return;
    }

    @Override
    public void play() {
        System.out.println("Police is playing!");
        if (!this.isAlive()) {
            return;
        }

        // This NPC is travelling
        if (travelManager.isTraveling()) {
            // If the police have scanned or fought someone during this travel, do nothing for the rest
            if (prevAction.equals(EntityActions.battle) || prevAction.equals(EntityActions.scan)) {
                this.travel();
                return;
            }

            prevAction = EntityActions.scan;
            var criminal = findCriminal();

            if (criminal.isPresent()) {
                var victim = entityManager.getEntity(criminal.getAsInt());
                prevAction = EntityActions.battle;
                Battle.fight(this, victim);
                System.out.println("Fought against a criminal!");
                if (!this.isAlive()) {
                    return;
                }
                criminalsManager.removeCriminal(criminal.getAsInt());
            }
        }

        // This NPC is at a planet. Do maintenance and sell all confiscated goods (if any).
        prevAction = EntityActions.maintenance;
        maintenance();
        if (!this.isEmpty()) {
            sell();
        }

        // Otherwise, travel to a random neighbor
        var neighbors = travelManager.getNeighbors().toArray(Integer[]::new);
        int randomIndex = new Random().nextInt(neighbors.length);

        prevAction = EntityActions.travelPrep;
        travelManager.travelStart(neighbors[randomIndex]);
    }

    private OptionalInt findCriminal() {
        for (var ID : travelManager.getPresentEntities()) {
            if (criminalsManager.isCriminal(ID)) {
                return OptionalInt.of(ID);
            }
        }

        return OptionalInt.empty();
    }

    private void maintenance() {
        // Shields always recharge when at a planet
        ownedShip.rechargeShields();
        ownedShip.repairHull();
        ownedShip.refuel();
    }

    private void sell() {
        var currPlanet = travelManager.getCurrLocation();
        for (var goodIndex : GoodsIndex.values()) {
            int index = goodIndex.ordinal();
            entityStats.credits += currPlanet.sell(index, entityStats.ownedGoods[index]);
            changeGoodsBy(index, -entityStats.ownedGoods[index]);
        }
    }

    private int getHomePlanetID() {
        return homePlanetID;
    }
}
