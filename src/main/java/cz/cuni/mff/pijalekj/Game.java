package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.battle.Battle;
import cz.cuni.mff.pijalekj.entities.EntityStats;
import cz.cuni.mff.pijalekj.entities.Planet;
import cz.cuni.mff.pijalekj.entities.Player;
import cz.cuni.mff.pijalekj.managers.CriminalsManager;
import cz.cuni.mff.pijalekj.managers.EntityManager;
import cz.cuni.mff.pijalekj.managers.LocationsManager;
import cz.cuni.mff.pijalekj.ships.Ship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Game  {
    private final LocationsManager locationsManager;
    private final CriminalsManager criminalsManager;
    private final EntityManager entityManager;
    private final GameClock clock = new GameClock();
    private final ArrayList<Input.Option> mainMenuOptions= new ArrayList<>();
    public Game(LocationsManager lm, CriminalsManager cm, EntityManager em) {
        this.locationsManager = lm;
        this.criminalsManager = cm;
        this.entityManager = em;
    }

    public void initGame() throws IOException {
        System.out.print("Welcome to SpaceTrader!\nEnter name: ");
        String name = Input.askString(String::isBlank);
    }

    public boolean play() {
        if (this.clock.tick()) {
            this.entityManager.resetNPCs(this.criminalsManager, this.locationsManager);
            this.locationsManager.updateAllPlanets();
            this.locationsManager.bigCheck(this.entityManager.getEntities().length);
        }
        this.playerPlay();
        HashMap<Integer, Integer> fightRequests = this.entityManager.play();
        this.handleBattles(fightRequests);
        this.criminalsManager.updateCriminals();

        return this.entityManager.getPlayer().isAlive();
    }

    private void handleBattles(HashMap<Integer, Integer> fightRequests) {
        // Player's request always has the highest priority
        int playerOpponent = fightRequests.getOrDefault(0, -1);
        if (playerOpponent > 0) {
            this.playerBattle(playerOpponent, true);
            fightRequests.remove(0);
        }
        for (var entry : fightRequests.entrySet()) {
            if (entry.getValue() == 0) {
                this.playerBattle(entry.getKey(), false);
                continue;
            }
            Battle battle = new Battle(this.entityManager.getEntity(entry.getKey()),
                    this.entityManager.getEntity(entry.getValue()));
            battle.fight();
        }
    }

    private void playerBattle(int opponentID, boolean startedByPlayer) {
        //TODO this
    }

    private void playerPlay() throws IOException {
        /*
        WORKFLOW:
        1) Show main screen with options
        2) Read input
        3) Perform the selected action
        4) GOTO 1)
        * */
        this.output.showMainScreen();
        Input.askOptions(
                new Input.Option("Quit game", this::quitGame),
                new Input.Option("Buy/sell goods", this::buySellGoods));
    }

    private void buySellGoods() {

    }
    private void quitGame() {

    }
    private void manageShip() {

    }
    private void

    private final Output output = new Output();
    private class Output {
        private void showMainScreen() {
            Player player = Game.this.entityManager.getPlayer();
            System.out.printf("Current planet: %s (%s)\n", player.getCurrPlanet().getName(),
                    player.getCurrPlanet().getPlanetType());
            System.out.printf("Credits: %d\n", player.getCredits());
            System.out.printf("Hull:  %s\n", xOutOfy(player.getCurrHull(), player.getMaxHull()));
            System.out.printf("Fuel:  %s\n", xOutOfy(player.getCurrFuel(), player.getMaxFuel()));
            System.out.printf("Cargo: %s\n", xOutOfy(player.getCurrCargo(), player.getMaxCargo()));
        }

        private static <T> String xOutOfy(T curr, T max) {
            return String.format("%s / %s", curr.toString(), max.toString());
        }
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
