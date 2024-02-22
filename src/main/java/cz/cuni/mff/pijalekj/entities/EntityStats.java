package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.GoodsIndex;

import java.util.Arrays;

public class EntityStats {
    private int credits;
    private final int[] ownedGoods;

    public EntityStats(int credits, int[] ownedGoods) {
        this.credits = credits;
        this.ownedGoods = ownedGoods;
    }

    public int getTotalGoodsAmount() {
        return Arrays.stream(this.ownedGoods).sum();
    }

    public int getCredits() {
        return this.credits;
    }

    public void addCredits(int credits) {
        assert credits >= 0;
        this.credits += credits;
    }

    public void removeCredits(int credits) {
        assert credits >= 0;
        this.credits -= credits;
    }

    public void transferAllCredits(EntityStats from) {
        this.addCredits(from.getCredits());
        from.removeCredits(from.getCredits());
    }

    public int getGoodAmount(int good) {
        return this.ownedGoods[good];
    }

    public void addGood(int good, int amount) {
        assert amount >= 0;
        this.ownedGoods[good] += amount;
    }

    public void removeGood(int good, int amount) {
        assert amount >= 0;
        assert this.ownedGoods[good] >= amount;
        this.ownedGoods[good] -= amount;
    }

    public int getGoodAmount(GoodsIndex good) {
        return this.ownedGoods[good.ordinal()];
    }

    public void addGood(GoodsIndex good, int amount) {
        assert amount >= 0;
        this.ownedGoods[good.ordinal()] += amount;
    }

    public void removeGood(GoodsIndex good, int amount) {
        assert amount >= 0;
        assert this.ownedGoods[good.ordinal()] >= amount;
        this.ownedGoods[good.ordinal()] -= amount;
    }

    public void transferAllGoods(EntityStats from, int limit) {
        assert limit >= 0;
        for (var index : GoodsIndex.values()) {
            int toTake = Math.min(from.getGoodAmount(index), limit);
            this.addGood(index, toTake);
            from.removeGood(index, toTake);
            limit -= toTake;
        }
    }
}
