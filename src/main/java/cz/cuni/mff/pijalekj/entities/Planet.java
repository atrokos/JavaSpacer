package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

import java.util.Random;

/**
 * The Planet class represents a celestial body in the game, with attributes such as name,
 * planet ID, goods and their prices; and industry type. It provides methods for buying and selling goods,
 * updating the planet's state, and computing prices.
 *
 * @param name        The name of the planet.
 * @param planetID    The unique identifier for the planet.
 * @param goodsPrices Goods and their prices associated with the planet.
 * @param planetType  The industry type of the planet.
 */
public record Planet(String name, PlanetIndustryType planetType, int planetID, GoodsPrices goodsPrices) {
    /**
     * Constructor for creating a new Planet object.
     *
     * @param name        The name of the planet.
     * @param planetType  The industry type of the planet.
     * @param planetID    The unique identifier for the planet.
     * @param goodsPrices Goods prices associated with the planet.
     */
    public Planet {
    }

    /**
     * Gets the name of the planet.
     *
     * @return The name of the planet.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Gets the unique identifier for the planet.
     *
     * @return The planet ID.
     */
    @Override
    public int planetID() {
        return planetID;
    }

    /**
     * Gets the goods prices associated with the planet.
     *
     * @return Goods prices of the planet.
     */
    @Override
    public GoodsPrices goodsPrices() {
        return goodsPrices;
    }

    /**
     * Gets the industry type of the planet.
     *
     * @return The industry type.
     */
    @Override
    public PlanetIndustryType planetType() {
        return planetType;
    }

    /**
     * Buys goods from the planet, updating the goods prices and returning the final price.
     *
     * @param good   The good to be bought from the planet.
     * @param amount How many to buy.
     * @return The final price (positive).
     */
    public int buy(int good, int amount) {
        goodsPrices.removeGood(good, amount);
        return amount * goodsPrices.getPrice(good);
    }

    /**
     * Sells goods to the planet, updating the goods prices and returning the final cost.
     *
     * @param good   The good to be sold to the planet.
     * @param amount How many to sell.
     * @return The final cost (positive).
     */
    public int sell(int good, int amount) {
        goodsPrices.addGood(good, amount);
        return amount * goodsPrices.getPrice(good);
    }

    /**
     * Updates the state of the planet, including industry, items, and prices.
     */
    public void update() {
        updateIndustry();
        updateItems();
        updatePrices();
    }

    /**
     * Updates the industry of the planet based on its type.
     */
    private void updateIndustry() {
        String baseKey = planetType().toString() + ".Production.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType;
            goodsPrices.addGood(goodType.ordinal(), Constants.goods.getLong(key).intValue());
        }
    }

    /**
     * Updates the prices of goods on the planet, introducing randomness.
     */
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

    /**
     * Updates the items on the planet based on its type.
     */
    private void updateItems() {
        String baseKey = planetType().toString() + ".Consumption.";

        for (var goodType : GoodsIndex.values()) {
            String key = baseKey + goodType;
            int by = Math.min(goodsPrices.getGoodAmount(goodType.ordinal()), Constants.goods.getLong(key).intValue());
            goodsPrices.removeGood(goodType.ordinal(), by);
        }
    }

    /**
     * Computes the new price based on the base price and offset.
     *
     * @param base   The base price.
     * @param offset The offset value.
     * @return The computed price.
     */
    private int computePrice(int base, int offset) {
        int newPrice = base - (offset / 3);
        if (newPrice <= 0) {
            newPrice = 20;
        }
        return newPrice;
    }
}