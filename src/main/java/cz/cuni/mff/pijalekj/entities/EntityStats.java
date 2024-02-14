package cz.cuni.mff.pijalekj.entities;

public class EntityStats {
    public int credits;
    public int[] ownedGoods;

    public EntityStats(int credits, int[] ownedGoods) {
        this.credits = credits;
        this.ownedGoods = ownedGoods;
    }
}
