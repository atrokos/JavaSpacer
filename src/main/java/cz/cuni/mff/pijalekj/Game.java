package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.entities.Entity;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;

public class Game {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();

    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        this.locationsManager = lm;
        this.criminalsManager = cm;
        this.entityManager = em;
    }

    public boolean play() {
        if (clock.tick()) {
            entityManager.resetNPCs(criminalsManager, locationsManager);
            locationsManager.updateAllPlanets();
            locationsManager.bigCheck(entityManager.getEntities().size());
        }

        boolean state = entityManager.play();
        criminalsManager.updateCriminals();
        return state;
    }

    public void initGame() throws IOException {
        System.out.print("Welcome to SpaceTrader!\nEnter name: ");
        String name = Input.askString(String::isBlank);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public LocationsManager getLocationsManager() {
        return locationsManager;
    }

    public GameClock getClock() {
        return clock;
    }

    private void playerPlay() {

    }

    private static class Input {
        private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        public record Option(String msg, Runnable action) {}
        private static void askOptions(Option... options) throws IOException {
            for (int i = 0; i < options.length; ++i) {
                System.out.printf("%d) %s\n", i, options[i].msg());
            }
            int optionNumber = askNumber(options.length);
            options[optionNumber].action.run();
        }

        private static int askNumber(int maximum) throws IOException {
            while (true) {
                String line = bufferedReader.readLine();
                try {
                    int optionNumber = Integer.parseInt(line);
                    if (optionNumber < 0 || optionNumber > maximum) {
                        throw new Exception();
                    }
                    return optionNumber;
                } catch (Exception ignored) {
                    System.out.println("Invalid option: " + line);
                }
            }
        }

        private static String askString(Predicate<String> predicate) throws IOException {
            String input;
            while (!predicate.test(input = bufferedReader.readLine())) {}

            return input;
        }
    }
}
