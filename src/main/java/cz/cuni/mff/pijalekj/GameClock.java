package cz.cuni.mff.pijalekj;

public class GameClock {
    private long ticks = 0;
    private final static long BIGTICK = 14;

    public GameClock(long ticks) {
        this.ticks = ticks;
    }
    public GameClock() {
        this.ticks = 0;
    }

    public boolean tick() {
        return this.ticks++ % BIGTICK == 0;
    }

    public long get_ticks() {
        return this.ticks;
    }
}
