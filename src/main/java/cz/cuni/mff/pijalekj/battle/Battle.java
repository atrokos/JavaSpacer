package cz.cuni.mff.pijalekj.battle;

import cz.cuni.mff.pijalekj.entities.Entity;

import java.util.ArrayList;
import java.util.Random;

public class Battle {
    private final Entity[] fighters;

    private final Random generator = new Random();

    public Battle(Entity attacker, Entity defender) {
        assert attacker.isAlive() && defender.isAlive();
        this.fighters = new Entity[] {attacker, defender};
    }

    public void fight() {
        int i = 0;
        while (true) {
            var attacking = this.fighters[i];
            var attacked = this.fighters[(i+1)%2];

            var attackerAction = attacking.battle(attacked);

            switch (attackerAction.actionType()) {
                case attack -> {
                    attacked.takeDamage(attackerAction.value());
                }
                case flee -> {
                    double dieThrow = this.generator.nextInt(0, 101);
                    if (dieThrow <= attackerAction.value()) {
                       return; // One party fled
                    }
                }
            }
            if (!attacked.isAlive()) {
                attacking.won(attacked);
                attacked.lost();
            }
            i = (i + 1) % 2;
        }
    }
}
