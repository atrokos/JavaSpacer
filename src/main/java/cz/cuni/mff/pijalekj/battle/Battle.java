package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.entities.Entity;

import java.util.Random;

/**
 * The Battle class represents a combat encounter between two entities in the game.
 * It manages the turn-based interactions between an attacker and a defender, with
 * actions such as attacking or fleeing. The battle continues until one of the entities
 * is defeated or one party successfully flees.
 */
public class Battle {
    private final Entity[] fighters;

    private final Random generator = new Random();

    /**
     * Constructs a Battle object with the given attacker and defender entities.
     * Both entities must be alive at the start of the battle.
     *
     * @param attacker The attacking entity.
     * @param defender The defending entity.
     * @throws AssertionError if either the attacker or defender is not alive.
     */
    public Battle(Entity attacker, Entity defender) {
        assert attacker.isAlive() && defender.isAlive();
        fighters = new Entity[]{attacker, defender};
    }

    /**
     * Initiates the battle, allowing the entities to take turns attacking and defending
     * until one of them is defeated or one party successfully flees.
     */
    public void fight() {
        int i = 0;
        while (true) {
            var attacking = fighters[i];
            var attacked = fighters[(i + 1) % 2];

            var attackerAction = attacking.battle(attacked);

            switch (attackerAction.actionType()) {
                case attack -> attacked.takeDamage(attackerAction.value());
                case flee -> {
                    double dieThrow = generator.nextInt(0, 101);
                    if (dieThrow <= attackerAction.value()) {
                        return; // One party fled
                    }
                }
            }
            if (!attacked.isAlive()) {
                attacking.won(attacked);
            }
            i = (i + 1) % 2;
        }
    }
}
