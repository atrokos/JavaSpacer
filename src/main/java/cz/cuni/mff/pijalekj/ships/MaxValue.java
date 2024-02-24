package cz.cuni.mff.pijalekj.ships;

/**
 * The MaxValue class represents a container for holding a current value and a maximum possible value.
 * It ensures that the current value does not exceed the specified maximum value, throwing an IllegalArgumentException if it does.
 */
public class MaxValue {
    private int curr;
    private final int max;

    /**
     * Constructs a MaxValue object with the same initial current and maximum values.
     *
     * @param curr_max The initial current and maximum value.
     */
    public MaxValue(int curr_max) {
        curr = curr_max;
        max = curr_max;
    }

    /**
     * Sets the current value to the specified value, ensuring it does not exceed the maximum value.
     *
     * @param value The value to set as the current value.
     * @throws IllegalArgumentException if the specified value is greater than the maximum value.
     */
    public void setCurr(int value) throws IllegalArgumentException {
        assert value <= max : "Value exceeds the maximum allowed value.";
        curr = value;
    }

    /**
     * Gets the maximum possible value.
     *
     * @return The maximum possible value.
     */
    public int getMax() {
        return max;
    }

    /**
     * Gets the current value.
     *
     * @return The current value.
     */
    public int getCurr() {
        return curr;
    }

    /**
     * Updates the current value by adding the specified value, ensuring it does not exceed the maximum value.
     *
     * @param value The value to add to the current value.
     * @throws IllegalArgumentException if the addition results in a value greater than the maximum value.
     */
    public void changeBy(int value) {
        assert curr + value <= max : "Value change exceeds the maximum allowed value.";
        curr += value;
    }

    /**
     * Sets the current value to the maximum value.
     */
    public void setToMax() {
        curr = max;
    }

    /**
     * Returns a string representation of the MaxValue object in the format "current/max".
     *
     * @return The formatted string representing the current and maximum values.
     */
    @Override
    public String toString() {
        return "%d/%d".formatted(curr, max);
    }
}

