package cz.cuni.mff.pijalekj.entities;

public class GoodsPrices {
    public int[] goods;
    public int[] prices;

    public GoodsPrices(int[] goods, int[] prices) {
        this.goods = goods;
        this.prices = prices;
    }

    public static void transferGoods(int[] from, int[] to) {
        for (int i = 0; i < from.length; ++i) {
            to[i] += from[i];
            from[i] = 0;
        }
    }
}
