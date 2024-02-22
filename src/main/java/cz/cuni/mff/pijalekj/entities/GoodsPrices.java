package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.GoodsIndex;

public class GoodsPrices {
    private final int[] goods;
    private final int[] prices;

    public GoodsPrices(int[] goods, int[] prices) {
        this.goods = goods;
        this.prices = prices;
    }

    public int getGoodAmount(int good) {
        assert this.goods[good] >= 0: "Good " + good + " is < 0!";
        return this.goods[good];
    }

    public int getGoodAmount(GoodsIndex good) {
        assert this.goods[good.ordinal()] >= 0: "Good " + good + " is < 0!";
        return this.goods[good.ordinal()];
    }

    public void addGood(int good, int amount) {
        assert amount >= 0: "Good " + good + " amount cannot be negative!";
        this.goods[good] += amount;
    }

    public void removeGood(int good, int amount) {
        assert amount >= 0: "Good " + good + " amount cannot be negative!";
        assert this.goods[good] >= amount: "Amount has to be larger than goods[good]!";
        this.goods[good] -= amount;
    }

    public int getPrice(int good) {
        return this.prices[good];
    }

    public int getPrice(GoodsIndex good) {
        return this.prices[good.ordinal()];
    }

    public void setPrice(int good, int price) {
        assert price >= 0;
        this.prices[good] = price;
    }
}
