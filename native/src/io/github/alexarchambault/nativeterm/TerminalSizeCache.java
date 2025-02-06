package io.github.alexarchambault.nativeterm;

import io.github.alexarchambault.nativeterm.internal.SigWinch;

/**
 * Helper caching the terminal size, and re-querying it only if a SIGWINCH has been received
 *
 * On Windows, this doesn't cache anything and just re-queries the terminal size upon
 * every [[getSize]] call.
 */
public final class TerminalSizeCache {
    /**
     * Create a new TerminalSizeCache
     *
     * This sets up a signal handle for the WINCH signal.
     *
     * As much as possible, only create a single instance of this class,
     * and re-use it through your application.
     */
    public TerminalSizeCache() {
        SigWinch.addHandler(
                new Runnable() {
                    @Override
                    public void run() {
                        terminalSize = null;
                    }
                }
        );
    }

    private TerminalSize terminalSize = null;

    /**
     * Get the terminal size
     *
     * If the terminal size is in cache, it's returned straightaway.
     *
     * If the terminal size isn't in cache (not queried yet or recently invalidated by a SIGWINCH),
     * it's queried, put in cache, and returned.
     *
     * @return the terminal size
     */
    public TerminalSize getSize() {
        if (NativeTerminal.isWindows)
            return NativeTerminal.getSize();

        TerminalSize terminalSize0 = terminalSize;
        if (terminalSize0 == null)
            terminalSize0 = update();
        return terminalSize0;
    }

    private TerminalSize update() {
        TerminalSize terminalSize0 = NativeTerminal.getSize();
        terminalSize = terminalSize0;
        return terminalSize0;
    }
}
