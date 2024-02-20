package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.managers.Battle;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.Random;

public class Pirate extends Entity {
    public Pirate(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager, Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);
    }

    @Override
    public BattleActionType battle(Entity opponent) {
        if (outSustain(opponent) > 0) {
            return BattleActionType.attack;
        }

        return BattleActionType.flee;
    }

    @Override
    public void won(Entity opponent) {
        takeAll(opponent);
        this.entityStats.transferAllCredits(opponent.entityStats);
    }

    @Override
    public void lost() {

    }

    @Override
    public void play() {
        System.out.println("Pirate is playing!");
        if (!this.isAlive()) {
            return;
        }

        if (travelManager.isTraveling()) {
            if (prevAction != EntityActions.battle && prevAction != EntityActions.scan) {
                findVictim();

                if (!this.isAlive()) {
                    return;
                }
            }

            travel();
            return;
        }

        maintenance();
        if (!this.isEmpty()) {
            sell();
        }
        prevAction = EntityActions.maintenance;

        if (ownedShip.getStats().fuel.getCurr() < 13) {
            return;
        }

        // Otherwise, travel to a random neighbor
        var neighbors = travelManager.getNeighbors().toArray(Integer[]::new);
        int randomIndex = new Random().nextInt(neighbors.length);

        prevAction = EntityActions.travelPrep;
        travelManager.travelStart(neighbors[randomIndex]);
    }

    private void findVictim() {
        var presentEnt = travelManager.getPresentEntities();
        int maxSustain = 0;
        int victimID = -1;
        prevAction = EntityActions.scan;

        for (var ID : presentEnt) {
            var entity = entityManager.getEntity(ID);
            // Ignore the worst ships
            switch (entity.getOwnedShip().getShipType()) {
                case ShipType.Gnat, ShipType.Flea -> {
                    continue;
                }
            }
            if (ID == entityID) {
                continue;
            }

            int sustain = outSustain(entity);
            if (sustain > maxSustain) {
                maxSustain = sustain;
                victimID = ID;
            }
        }

        if (victimID != -1) {
            if (presentEnt.size() > 2) {
                criminalsManager.addCriminal(entityID);
            }

            Battle.fight(this, entityManager.getEntity(victimID));
            prevAction = EntityActions.battle;
        }
    }
}
