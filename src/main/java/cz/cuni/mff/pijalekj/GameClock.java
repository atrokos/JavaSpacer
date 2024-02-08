package cz.cuni.mff.pijalekj;

public class GameClock {
    private long ticks = 0;
    private final long BIGTICK;

    public GameClock(long ticks, long bigTick) {
        this.ticks = ticks;
        this.BIGTICK = bigTick;
    }

    public boolean tick() {
        return ++ticks % BIGTICK == 0;
    }

    public long get_ticks() {
        return ticks;
    }
}
