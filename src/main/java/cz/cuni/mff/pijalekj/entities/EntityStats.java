package cz.cuni.mff.pijalekj.entities;

public class EntityStats {
    public long credits;
    public int[] ownedGoods;

    public EntityStats(long credits, int[] ownedGoods) {
        this.credits = credits;
        this.ownedGoods = ownedGoods;
    }
}
