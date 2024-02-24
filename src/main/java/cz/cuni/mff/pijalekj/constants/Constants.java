package cz.cuni.mff.pijalekj.constants;

import com.moandjiezana.toml.Toml;

import java.io.File;

/**
 * The Constants class holds static fields representing various constants used in the application.
 * It includes configurations for goods, entities, ships, builders, and other numerical values.
 */
public class Constants {
    /** Configuration for goods containing numerical data. */
    public final static Toml goods = new Toml().read(new File("./src/data/goodsNumbers.toml"));

    /** Configuration for entities containing numerical data. */
    public final static Toml entities = new Toml().read(new File("./src/data/entityNumbers.toml"));

    /** Configuration for ships containing numerical data. */
    public final static Toml ships = new Toml().read(new File("./src/data/shipNumbers.toml"));

    /** Configuration for builders containing numerical data. */
    public final static Toml builders = new Toml().read(new File("./src/data/builderData.toml"));

    /** Cost of fuel used for calculations. */
    public final static int fuelCost = 3;

    /** Cost of ship repair used for calculations. */
    public final static int repairCost = 5;

    /** Coefficient used in battle calculations. */
    public static final double BATTLE_COEFF = 130.;

    /** Default timeout value for the CriminalsList. */
    public static final int DEFAULT_TIMEOUT = 40;
}

