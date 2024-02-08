package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.entities.GoodsPrices;
import cz.cuni.mff.pijalekj.enums.PlanetIndustryType;

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
    public int sellBuy(int[] goods, int credits) {
        // TODO
    }
    public void update() {
        updateIndustry();
        updatePrices();
    }
    private void updateIndustry() {
        // TODO
    }
    private void updatePrices() {
        // TODO
    }
    private void updateItems() {
        // TODO
    }
    private void setPrice(int good, int base, int offset) {
        // TODO
    }
    private void consumeGood(int good, int by) {
        // TODO
    }
}