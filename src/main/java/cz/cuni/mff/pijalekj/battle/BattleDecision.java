package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.enums.BattleActionType;

public record BattleDecision(BattleActionType actionType, int value) {
}
