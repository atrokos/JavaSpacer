package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.BattleActionType;
import cz.cuni.mff.pijalekj.enums.EntityActions;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.TravelManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import static org.fusesource.jansi.Ansi.*;

public class Player extends Entity {
    private String name;
    public Player(TravelManager travelManager, EntityManager entityManager, CriminalsManager criminalsManager,
                  Ship ownedShip, EntityStats entityStats, EntityActions prevAction, int entityID, String name) {
        super(travelManager, entityManager, criminalsManager, ownedShip, entityStats, prevAction, entityID);

        this.name = name;
    }

    @Override
    public BattleActionType battle(Entity opponent) {
        return null;
    }

    @Override
    public void won(Entity opponent) {

    }

    @Override
    public void lost() {

    }

    @Override
    public void play() {
        if (!this.isAlive()) {
            return;
        }

        if (this.travelManager.isTraveling()) {
            if (prevAction != EntityActions.scan) {
                this.battleTraveling();
                return;
            }
            this.travel();
        }


    }

    private void battleTraveling() {
        var chosenEntity = this.travelManager.getPresentEntities().stream()
                .filter(ID -> ID != this.entityID)
                .findFirst();
        if (chosenEntity.isEmpty()) {
            return;
        }

        System.out.print(ansi().eraseScreen());
        System.out.print(ansi().a("While traveling, you meet a ship:\n"));
        // TODO rest
    }

    public void setName(String name) {
        this.name = name;
    }
}
