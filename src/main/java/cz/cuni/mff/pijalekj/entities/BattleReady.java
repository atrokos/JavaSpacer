package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.BattleActionType;

public interface BattleReady {
    BattleActionType battle(Entity opponent);
    void won(Entity opponent);
    void lost();
}
