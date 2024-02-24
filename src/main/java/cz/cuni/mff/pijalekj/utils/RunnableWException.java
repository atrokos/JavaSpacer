package cz.cuni.mff.pijalekj.utils;

import java.io.IOException;

/**
 * A functional interface similar to Runnable, but enables the use of methods
 * that throw IOExceptions.
 */
@FunctionalInterface
public interface RunnableWException {
    void run() throws IOException;
}
