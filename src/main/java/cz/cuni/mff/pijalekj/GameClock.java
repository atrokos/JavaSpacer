package cz.cuni.mff.pijalekj;

public class GameClock {
    private long ticks;
    private final static long BIGTICK = 14;

    public GameClock(long ticks) {
        this.ticks = ticks;
    }
    public GameClock() {
        ticks = 0;
    }

    public boolean tick() {
        return ticks++ % BIGTICK == 0;
    }

    public long get_ticks() {
        return ticks;
    }
}
