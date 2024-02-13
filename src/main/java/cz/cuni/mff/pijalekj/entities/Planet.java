package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

import java.util.Random;

public class Planet {
    protected final String name;
    protected final long planetID;
    protected GoodsPrices goodsPrices;
    protected final PlanetIndustryType planetType;
    public Planet(String name, long planetID, GoodsPrices goodsPrices, PlanetIndustryType planetType) {
        this.name = name;
        this.planetID = planetID;
        this.goodsPrices = goodsPrices;
        this.planetType = planetType;
    }
    public String getName() {
        return name;
    }
    public long getPlanetID() {
        return planetID;
    }
    public GoodsPrices getGoodsPrices() {
        return goodsPrices;
    }
    public PlanetIndustryType getPlanetType() {
        return planetType;
    }

    // Goods - all goods of the entity; amount - what to sell/buy;
    // credits - entity's credits, to check if it can afford it
    public int sellBuy(int[] goods, int[] toSell, int credits) {
        // TODO - promyslet jinak, takhle to moc nedava smysl
        throw new IllegalArgumentException("This method was not implemented yet!");
    }
    public void update() {
        updateIndustry();
        updateItems();
        updatePrices();
    }
    private void updateIndustry() {
        var goods = goodsPrices.goods;
        String baseKey = getPlanetType().toString() + ".Production.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType.toString();
            goods[goodType.ordinal()] += Constants.goods.getLong(key);
        }
    }
    private void updatePrices() {
        Random random = new Random();
        var goods = goodsPrices.goods;
        var prices = goodsPrices.prices;

        String baseKey = "BasePrices.";
        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType.toString();
            int basePrice = Constants.goods.getLong(key).intValue() + random.nextInt(0, 21);
            int offset = goods[goodType.ordinal()];

            prices[goodType.ordinal()] = computePrice(basePrice, offset);
        }
    }
    private void updateItems() {
        var goods = goodsPrices.goods;
        String baseKey = getPlanetType().toString() + ".Consumption.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType.toString();
            int by = Constants.goods.getLong(key).intValue();
            int origAmount = goods[goodType.ordinal()];
            goods[goodType.ordinal()] = consumeGood(origAmount, by);
        }
    }
    private int computePrice(int base, int offset) {
        int newPrice = base - (offset / 3);
        if (newPrice <= 0) {
            newPrice = 20;
        }
        return newPrice;
    }
    private int consumeGood(int origAmount, int by) {
        return by > origAmount ? 0 : origAmount - by;
    }
}