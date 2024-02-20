package cz.cuni.mff.pijalekj.ships;

/**
 * A class that holds current value and a maximum possible one.
 * Throws IllegalArgumentException when a higher value than max is given to current.
 */
public class MaxValue {
    private int curr;
    private final int max;

    public MaxValue(int curr, int max) {
        this.curr = curr;
        this.max = max;
    }

    public MaxValue(int curr_max) {
        this.curr = curr_max;
        this.max = curr_max;
    }

    public void setCurr(int value) throws IllegalArgumentException {
        assert value <= max: "Tuple was given higher value than its maximum.";
        curr = value;
    }

    public int getMax() {
        return max;
    }

    public int getCurr() {
        return curr;
    }

    public void changeBy(int value) {
        assert curr + value <= max;
        curr += value;
    }

    // Sets the current value to the maximum.
    public void setToMax() {
        curr = max;
    }
}
