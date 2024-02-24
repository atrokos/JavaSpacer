package cz.cuni.mff.pijalekj.entities;

import cz.cuni.mff.pijalekj.enums.GoodsIndex;

import java.util.Arrays;

/**
 * The EntityStats class represents the statistics and inventory of an entity in the game.
 * It includes information about credits and owned goods.
 */
public class EntityStats {
    private int credits;
    private final int[] ownedGoods;

    /**
     * Constructs an EntityStats object with specified initial credits and goods.
     *
     * @param credits     The initial credits.
     * @param ownedGoods  An array representing the initial amounts of each type of goods.
     */
    public EntityStats(int credits, int[] ownedGoods) {
        this.credits = credits;
        this.ownedGoods = ownedGoods;
    }

    /**
     * Gets the total amount of goods owned by the entity.
     *
     * @return The total amount of goods.
     */
    public int getTotalGoodsAmount() {
        return Arrays.stream(ownedGoods).sum();
    }

    /**
     * Gets the current amount of credits owned by the entity.
     *
     * @return The amount of credits.
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Adds credits to the entity's balance.
     *
     * @param credits The amount of credits to add.
     */
    public void addCredits(int credits) {
        assert credits >= 0;
        this.credits += credits;
    }

    /**
     * Removes credits from the entity's balance.
     *
     * @param credits The amount of credits to remove.
     */
    public void removeCredits(int credits) {
        assert credits >= 0;
        this.credits -= credits;
    }

    /**
     * Transfers all credits from another EntityStats object to this object.
     *
     * @param from The EntityStats object from which to transfer credits.
     */
    public void transferAllCredits(EntityStats from) {
        addCredits(from.getCredits());
        from.removeCredits(from.getCredits());
    }

    /**
     * Gets the amount of a specific type of good owned by the entity.
     *
     * @param good The index of the good.
     * @return The amount of the specified good.
     */
    public int getGoodAmount(int good) {
        return ownedGoods[good];
    }

    /**
     * Adds a specified amount of a good to the entity's inventory.
     *
     * @param good   The index of the good.
     * @param amount The amount to add.
     */
    public void addGood(int good, int amount) {
        assert amount >= 0;
        ownedGoods[good] += amount;
    }

    /**
     * Removes a specified amount of a good from the entity's inventory.
     *
     * @param good   The index of the good.
     * @param amount The amount to remove.
     */
    public void removeGood(int good, int amount) {
        assert amount >= 0;
        assert ownedGoods[good] >= amount;
        ownedGoods[good] -= amount;
    }

    /**
     * Gets the amount of a specific type of good owned by the entity using GoodsIndex enum.
     *
     * @param good The GoodsIndex of the good.
     * @return The amount of the specified good.
     */
    public int getGoodAmount(GoodsIndex good) {
        return ownedGoods[good.ordinal()];
    }

    /**
     * Adds a specified amount of a good to the entity's inventory using GoodsIndex enum.
     *
     * @param good   The GoodsIndex of the good.
     * @param amount The amount to add.
     */
    public void addGood(GoodsIndex good, int amount) {
        assert amount >= 0;
        ownedGoods[good.ordinal()] += amount;
    }

    /**
     * Removes a specified amount of a good from the entity's inventory using GoodsIndex enum.
     *
     * @param good   The GoodsIndex of the good.
     * @param amount The amount to remove.
     */
    public void removeGood(GoodsIndex good, int amount) {
        assert amount >= 0;
        assert ownedGoods[good.ordinal()] >= amount;
        ownedGoods[good.ordinal()] -= amount;
    }

    /**
     * Transfers all goods (up to a specified limit) from another EntityStats object to this object.
     *
     * @param from  The EntityStats object from which to transfer goods.
     * @param limit The maximum amount of goods to transfer.
     */
    public void transferAllGoods(EntityStats from, int limit) {
        assert limit >= 0;
        for (var index : GoodsIndex.values()) {
            int toTake = Math.min(from.getGoodAmount(index), limit);
            addGood(index, toTake);
            from.removeGood(index, toTake);
            limit -= toTake;
        }
    }
}
