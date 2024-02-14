package cz.cuni.mff.pijalekj.builders;

import cz.cuni.mff.pijalekj.constants.Constants;
import cz.cuni.mff.pijalekj.entities.GoodsPrices;
import cz.cuni.mff.pijalekj.enums.GoodsIndex;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

public class GoodsBuilder {
    public static GoodsPrices defaultGoodsPrices(PlanetIndustryType type) {
        String goodKey = type.toString() + ".Consumption.";
        String priceKey = "BasePrices.";
        var goods = new int[9];
        var prices = new int[9];

        for (var goodIndex : GoodsIndex.values()) {
            int amount = Constants.goods.getLong(goodKey + goodIndex).intValue();
            int price = Constants.goods.getLong(priceKey + goodIndex).intValue();

            goods[goodIndex.ordinal()] = amount;
            prices[goodIndex.ordinal()] = price;
        }

        return new GoodsPrices(goods, prices);
    }
    public static GoodsPrices emptyGoodsPrices(PlanetIndustryType type) {
        var goods = new int[9];
        var prices = new int[9];

        return new GoodsPrices(goods, prices);
    }
}
