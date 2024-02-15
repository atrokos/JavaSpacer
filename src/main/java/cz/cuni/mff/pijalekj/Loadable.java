package cz.cuni.mff.pijalekj;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import cz.cuni.mff.pijalekj.utils.ColorConsole;


public interface Loadable<T> {
    void save(TomlWriter writer);
    T load(Toml toml);
}
