package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.enums.BattleActionType;

/**
 * Tuple describing the action the Entity took in battle and its value.
 * @param actionType The type of action the Entity took.
 * @param value Either the damage or the chance to flee, depending on the action.
 */
public record BattleDecision(BattleActionType actionType, int value) {
}
