package io.github.alexarchambault.nativeterm;

import io.github.alexarchambault.isterminal.IsTerminal;
import io.github.alexarchambault.nativeterm.internal.UnixTerm;
import io.github.alexarchambault.nativeterm.internal.WindowsTerm;

import java.io.IOException;

public final class NativeTerminal {
    private NativeTerminal() {}

    final static boolean isWindows;

    static {
        isWindows = System.getProperty("os.name")
                .toLowerCase(java.util.Locale.ROOT)
                .contains("windows");
    }

    public static TerminalSize getSize() {
        if (isWindows)
            return WindowsTerm.getSize();
        return UnixTerm.getSize(true);
    }

    public static boolean setupAnsi() throws IOException {
        if (isWindows && IsTerminal.isTerminal())
            return WindowsTerm.setupAnsi();
        return true;
    }
}
