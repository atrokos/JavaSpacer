package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.battle.BattleDecision;
import cz.cuni.mff.pijalekj.battle.Playerlike;
import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.OptionalInt;
import java.util.Random;

/**
 * The Pirate class, representing a type of Entity specialized in battles and attacking other entities.
 * Extends the abstract Entity class.
 */
public class Pirate extends Entity {

    /**
     * Constructs a Pirate with specified attributes.
     *
     * @param travelManager   The TravelManager for managing travel-related functionalities.
     * @param entityManager   The EntityManager for managing entities within the game.
     * @param criminalsManager The CriminalsManager for managing criminal entities.
     * @param ownedShip       The Ship owned by the pirate.
     * @param entityStats     The statistics of the pirate entity.
     * @param prevAction      The previous action performed by the pirate.
     * @param entityID        The unique identifier for the pirate entity.
     */
    public Pirate(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID) {
        super(travelManager, entityManager, ownedShip, entityStats, prevAction, entityID);
    }

    /**
     * Performs a battle against an opponent, deciding whether to attack or flee based on sustainability.
     *
     * @param opponent The opponent entity to battle.
     * @return A BattleDecision indicating the pirate's chosen battle action.
     */
    public BattleDecision battle(Playerlike opponent) {
        if (outSustain(opponent.getOwnedShip()) > 0) {
            return new BattleDecision(BattleActionType.attack,
                    Ship.damageOutput(ownedShip, opponent.getOwnedShip()));
        }

        return new BattleDecision(BattleActionType.flee, ownedShip.getFleeChance());
    }

    /**
     * Actions to be performed when the pirate wins a battle against an opponent.
     *
     * @param opponent The opponent entity that the pirate defeated.
     */
    @Override
    public void won(Playerlike opponent) {
        takeAll(opponent.getEntityStats());
        entityStats.transferAllCredits(opponent.getEntityStats());
    }

    /**
     * Determines the actions to be performed during a turn, considering the pirate's current state.
     *
     * @return An OptionalInt indicating the chosen action or an empty OptionalInt if no action is taken.
     */
    @Override
    public OptionalInt play() {
        if (!isAlive()) {
            return OptionalInt.empty();
        }

        if (travelManager.isTraveling()) {
            if (prevAction != EntityActions.battle && prevAction != EntityActions.scan) {
                return findVictim();
            }

            travel();
            return OptionalInt.empty();
        }

        maintenance();
        if (isFull()) {
            sell();
        }
        prevAction = EntityActions.maintenance;

        if (ownedShip.getStats().fuel.getCurr() < 13) {
            return OptionalInt.empty();
        }

        // Otherwise, travel to a random neighbor
        var neighbors = travelManager.getNeighbors();
        int randomIndex = new Random().nextInt(neighbors.length);

        prevAction = EntityActions.travelPrep;
        travelManager.travelStart(neighbors[randomIndex]);
        return OptionalInt.empty();
    }

    /**
     * Finds a potential victim for the pirate to attack based on sustainability.
     *
     * @return An OptionalInt indicating the chosen victim's ID, or an empty OptionalInt if no victim is found.
     */
    private OptionalInt findVictim() {
        var presentEnt = travelManager.getPresentEntities();
        int maxSustain = 0;
        int victimID = -2;
        prevAction = EntityActions.scan;

        for (var ID : presentEnt) {
            var entityShip = entityManager.getEntityShip(ID);
            // Ignore the worst ships
            switch (entityShip.getShipType()) {
                case Gnat, Flea -> {
                    continue;
                }
            }
            if (ID == entityID) {
                continue;
            }

            int sustain = outSustain(entityShip);
            if (sustain > maxSustain) {
                maxSustain = sustain;
                victimID = ID;
            }
        }

        if (victimID != -2) {
            prevAction = EntityActions.battle;
            return OptionalInt.of(victimID);
        }

        return OptionalInt.empty();
    }
}

