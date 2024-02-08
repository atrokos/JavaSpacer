package cz.cuni.mff.pijalekj;

import cz.cuni.mff.pijalekj.utils.ColorConsole;


/**
 * An object that can, by itself, output to the classes of PrintStream.
 */
public interface Outputtable {
    void setOutput(ColorConsole output);
}
