package io.github.alexarchambault.nativeterm.internal;

import io.github.alexarchambault.nativeterm.TerminalSize;
import org.fusesource.jansi.internal.CLibrary;

import static org.fusesource.jansi.internal.CLibrary.*;

/**
 * Utilities to get the terminal size on Linux / Mac
 */
public final class UnixTerm {

    private UnixTerm() {}

    /**
     * Gets the terminal size on Linux / Mac
     * @param useStdout whether to use stdout or stderr
     * @return the terminal size
     */
    public static TerminalSize getSize(boolean useStdout) {
        WinSize sz = new WinSize();
        int fd = useStdout ? STDOUT_FILENO : STDERR_FILENO;
        ioctl(fd, CLibrary.TIOCGWINSZ, sz);
        return TerminalSize.of(sz.ws_col, sz.ws_row);
    }

}
