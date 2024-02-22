package cz.cuni.mff.pijalekj.managers;

import cz.cuni.mff.pijalekj.constants.Constants;

import java.util.HashMap;

public class CriminalsManager extends HashMap<Integer, Integer> {
    public boolean isCriminal(int entityID) {
        return this.containsKey(entityID);
    }

    public void addCriminal(int entityID) {
        this.put(entityID, Constants.DEFAULT_TIMEOUT);
    }

    public void removeCriminal(int entityID) {
        this.remove(entityID);
    }

    public void updateCriminals() {
        this.replaceAll((key, value) -> value - 1);
        this.entrySet().removeIf(item -> item.getValue() <= 0);
    }
}
