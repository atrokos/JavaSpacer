package cz.cuni.mff.pijalekj.constants;

import com.moandjiezana.toml.Toml;

import java.io.File;

public class Constants {
    public final static Toml goods = new Toml().read(new File("./src/data/goodsNumbers.toml"));
    public final static Toml entities = new Toml().read(new File("./src/data/entityNumbers.toml"));
    public final static Toml ships = new Toml().read(new File("./src/data/shipNumbers.toml"));

}
