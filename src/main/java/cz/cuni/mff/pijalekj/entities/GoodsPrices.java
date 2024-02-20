package cz.cuni.mff.pijalekj.entities;

public class GoodsPrices {
    private int[] goods;
    private int[] prices;

    public GoodsPrices(int[] goods, int[] prices) {
        this.goods = goods;
        this.prices = prices;
    }

    public int getGoodAmount(int good) {
        assert goods[good] >= 0: "Good " + good + " is < 0!";
        return goods[good];
    }

    public void addGood(int good, int amount) {
        assert amount >= 0: "Good " + good + " amount cannot be negative!";
        goods[good] += amount;
    }

    public void removeGood(int good, int amount) {
        assert amount >= 0: "Good " + good + " amount cannot be negative!";
        assert goods[good] >= amount: "Amount has to be larger than goods[good]!";
        goods[good] -= amount;
    }

    public int getPrice(int good) {
        return prices[good];
    }

    public void setPrice(int good, int price) {
        assert price >= 0;
        prices[good] = price;
    }
}
