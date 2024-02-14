package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.entities.BattleReady;
import cz.cuni.mff.pijalekj.entities.Entity;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.util.Random;

public class Battle {
    private static final Random generator = new Random();

    public static BattleResult fight(Entity attacker, Entity defender) {
        while (true) {
            var attackerAction = attacker.battle(defender);

            switch (attackerAction) {
                case attack -> {
                    int damageDone = Ship.damageOutput(attacker.getOwnedShip(), defender.getOwnedShip());
                    defender.getOwnedShip().takeDamage(damageDone);
                }
                case flee -> {
                    double dieThrow = generator.nextDouble(0., 101.);
                    if (dieThrow <= attacker.getOwnedShip().getFleeChance()) {
                        return aftermath(BattleResult.attackerFled, attacker, defender);
                    }
                }
            }

            if (!defender.isAlive()) {
                return aftermath(BattleResult.attackerWon, attacker, defender);
            }

            var defenderAction = defender.battle(attacker);

            switch (defenderAction) {
                case attack -> {
                    int damageDone = Ship.damageOutput(defender.getOwnedShip(), attacker.getOwnedShip());
                    attacker.getOwnedShip().takeDamage(damageDone);
                }
                case flee -> {
                    double dieThrow = generator.nextDouble(0., 101.);
                    if (dieThrow <= defender.getOwnedShip().getFleeChance()) {
                        return aftermath(BattleResult.defenderFled, attacker, defender);
                    }
                }
            }

            if (!attacker.isAlive()) {
                return aftermath(BattleResult.defenderWon, attacker, defender);
            }
        }
    }

    public static BattleResult aftermath(BattleResult result, Entity attacker, Entity defender) {
        switch (result) {
            case attackerWon -> {
                attacker.won(defender);
                defender.lost();
            }
            case defenderWon -> {
                defender.won(attacker);
                attacker.lost();
            }
            case attackerFled, defenderFled -> {
            }
        }

        return result;
    }

    public enum BattleResult {
        attackerWon, attackerFled, defenderWon, defenderFled
    }
}
