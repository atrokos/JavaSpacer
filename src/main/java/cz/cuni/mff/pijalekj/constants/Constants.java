package cz.cuni.mff.pijalekj.constants;

import com.moandjiezana.toml.Toml;

import java.io.File;

public class Constants {
    public final static Toml goods = new Toml().read(new File("./src/data/goodsNumbers.toml"));
    public final static Toml entities = new Toml().read(new File("./src/data/entityNumbers.toml"));
    public final static Toml ships = new Toml().read(new File("./src/data/shipNumbers.toml"));
    public final static Toml builders = new Toml().read(new File("./src/data/builderData.toml"));
    public final static Toml strings = new Toml().read(new File("./src/data/stringData.toml"));
    public final static int fuelCost = 3;
    public final static int repairCost = 5;
    public static final double BATTLE_COEFF = 130.;
    public static final int DEFAULT_TIMEOUT = 40;
}
