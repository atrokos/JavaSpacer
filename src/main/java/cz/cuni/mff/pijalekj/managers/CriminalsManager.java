package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.utils.Constants;

import java.util.HashMap;

public class CriminalsManager {
    HashMap<Long, Integer> criminals;

    public CriminalsManager() {
        this.criminals = new HashMap<>();
    }

    public CriminalsManager(HashMap<Long, Integer> criminals) {
        this.criminals = criminals;
    }

    public boolean isCriminal(long entityID) {
        return criminals.containsKey(entityID);
    }

    public void addCriminal(long entityID) {
        criminals.put(entityID, Constants.DEFAULT_TIMEOUT);
    }

    public void removeCriminal(long entityID) {
        criminals.remove(entityID);
    }

    public void updateCriminals() {
        criminals.replaceAll((key, value) -> value - 1);
        criminals.entrySet().removeIf(item -> item.getValue() <= 0);
    }
}
