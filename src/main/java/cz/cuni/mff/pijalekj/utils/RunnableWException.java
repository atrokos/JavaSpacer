package cz.cuni.mff.pijalekj.utils;

import java.io.IOException;

@FunctionalInterface
public interface RunnableWException {
    void run() throws IOException;
}
