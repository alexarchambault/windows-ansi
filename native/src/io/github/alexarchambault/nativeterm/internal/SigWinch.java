package io.github.alexarchambault.nativeterm.internal;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Helper to register handlers of the WINCH signal (terminal size change)
 */
public final class SigWinch {
    private SigWinch() {}

    /**
     * Register a new WINCH handler
     * @param runnable A Runnable, run every time a WINCH signal is received
     */
    public static void addHandler(Runnable runnable) {

        SignalHandler handler = new SignalHandler() {
            @Override
            public void handle(Signal arg0) {
                runnable.run();
            }
        };

        Signal.handle(new Signal("WINCH"), handler);
    }

}
