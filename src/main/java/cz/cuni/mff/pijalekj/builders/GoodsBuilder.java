package cz.cuni.mff.pijalekj.builders;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.entities.GoodsPrices;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

/**
 * The GoodsBuilder class provides static methods for constructing goods-related objects,
 * such as goods prices for a specific planet industry type.
 */
public class GoodsBuilder {
    /**
     * Generates default goods prices based on a given planet industry type.
     *
     * @param type  The planet industry type for which goods prices are generated.
     * @return      The GoodsPrices object representing the default goods prices.
     */
    public static GoodsPrices defaultGoodsPrices(PlanetIndustryType type) {
        String goodKey = type.toString() + ".Consumption.";
        String priceKey = "BasePrices.";
        var goods = new int[9];
        var prices = new int[9];

        for (var goodIndex : GoodsIndex.values()) {
            int amount = 3 * Constants.goods.getLong(goodKey + goodIndex).intValue();
            int price = Constants.goods.getLong(priceKey + goodIndex).intValue();

            goods[goodIndex.ordinal()] = amount;
            prices[goodIndex.ordinal()] = price;
        }

        return new GoodsPrices(goods, prices);
    }
}
