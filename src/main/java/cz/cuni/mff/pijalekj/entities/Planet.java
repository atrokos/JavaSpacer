package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Planet {
    private final String name;
    private final long planetID;
    private final GoodsPrices goodsPrices;
    private final PlanetIndustryType planetType;
    public Planet(String name, PlanetIndustryType planetType, long planetID, GoodsPrices goodsPrices) {
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

    // Good - the good to be bought from the planet; amount - how many to buy;
    // returns the final price (positive)
    public int buy(int good, int amount) {
        goodsPrices.removeGood(good, amount);
        return amount * goodsPrices.getPrice(good);
    }

    // Good - the good to be sold to the planet; amount - how many to sell
    // Returns the final cost (positive)
    public int sell(int good, int amount) {
        goodsPrices.addGood(good, amount);
        return amount * goodsPrices.getPrice(good);
    }
    public void update() {
        updateIndustry();
        updateItems();
        updatePrices();
    }

    private void updateIndustry() {
        String baseKey = getPlanetType().toString() + ".Production.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType;
            goodsPrices.addGood(goodType.ordinal(), Constants.goods.getLong(key).intValue());
        }
    }
    private void updatePrices() {
        Random random = new Random();

        String baseKey = "BasePrices.";
        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType;
            int basePrice = Constants.goods.getLong(key).intValue() + random.nextInt(0, 21);
            int offset = goodsPrices.getGoodAmount(goodType.ordinal());

            goodsPrices.setPrice(goodType.ordinal(), computePrice(basePrice, offset));
        }
    }
    private void updateItems() {
        String baseKey = getPlanetType().toString() + ".Consumption.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType;
            int by = Math.min(goodsPrices.getGoodAmount(goodType.ordinal()), Constants.goods.getLong(key).intValue());
            goodsPrices.removeGood(goodType.ordinal(), by);
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
        int new_amount = origAmount - by;
        return Math.max(0, new_amount);
    }
}