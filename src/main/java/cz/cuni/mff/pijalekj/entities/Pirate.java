package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.enums.ShipType;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;
import java.util.Random;

public class Pirate extends Entity {
    public Pirate(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager, Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);
    }

    public BattleDecision battle(Entity opponent) {
        if (this.outSustain(opponent.getOwnedShip()) > 0) {
            return new BattleDecision(BattleActionType.attack,
                    Ship.damageOutput(this.ownedShip, opponent.getOwnedShip()));
        }

        return new BattleDecision(BattleActionType.flee, this.ownedShip.getFleeChance());
    }

    @Override
    public void won(Entity opponent) {
        this.takeAll(opponent.getEntityStats());
        this.entityStats.transferAllCredits(opponent.getEntityStats());
    }

    @Override
    public void lost() {

    }

    @Override
    public OptionalInt play() {
        System.out.println("Pirate is playing!");
        if (!this.isAlive()) {
            return OptionalInt.empty();
        }

        if (this.travelManager.isTraveling()) {
            if (this.prevAction != EntityActions.battle && this.prevAction != EntityActions.scan) {
                return this.findVictim();
            }

            this.travel();
            return OptionalInt.empty();
        }

        this.maintenance();
        if (this.isFull()) {
            this.sell();
        }
        this.prevAction = EntityActions.maintenance;

        if (this.ownedShip.getStats().fuel.getCurr() < 13) {
            return OptionalInt.empty();
        }

        // Otherwise, travel to a random neighbor
        var neighbors = this.travelManager.getNeighbors().toArray(Integer[]::new);
        int randomIndex = new Random().nextInt(neighbors.length);

        this.prevAction = EntityActions.travelPrep;
        this.travelManager.travelStart(neighbors[randomIndex]);
        return OptionalInt.empty();
    }

    private OptionalInt findVictim() {
        var presentEnt = this.travelManager.getPresentEntities();
        int maxSustain = 0;
        int victimID = -1;
        this.prevAction = EntityActions.scan;

        for (var ID : presentEnt) {
            var entityShip = this.entityManager.getEntityShip(ID);
            // Ignore the worst ships
            switch (entityShip.getShipType()) {
                case ShipType.Gnat, ShipType.Flea -> {
                    continue;
                }
            }
            if (ID == this.entityID) {
                continue;
            }

            int sustain = this.outSustain(entityShip);
            if (sustain > maxSustain) {
                maxSustain = sustain;
                victimID = ID;
            }
        }

        if (victimID != -1) {
            if (presentEnt.size() > 2) {
                this.criminalsManager.addCriminal(this.entityID);
            }
            this.prevAction = EntityActions.battle;
            return OptionalInt.of(victimID);
        }

        return OptionalInt.empty();
    }
}
