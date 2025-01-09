package io.github.alexarchambault.nativeterm.internal;

import io.github.alexarchambault.nativeterm.TerminalSize;
import org.fusesource.jansi.internal.Kernel32;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public final class WindowsTerm {

    static {
        // Workaround while we can't benefit from https://github.com/fusesource/jansi/pull/292
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        if (arch.equals("arm64") || arch.equals("aarch64")) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL dllUrl = cl.getResource("org/fusesource/jansi/internal/native/Windows/arm64/jansi.dll");
            URL soUrl = cl.getResource("org/fusesource/jansi/internal/native/Windows/arm64/libjansi.so");
            if (dllUrl == null && soUrl != null) {
                System.setProperty("library.jansi.name", "libjansi.so");
            }
        }

        // Make https://github.com/fusesource/hawtjni/blob/c14fec00b9976ff6b84e62e483d678594a7d3832/hawtjni-runtime/src/main/java/org/fusesource/hawtjni/runtime/Library.java#L167 happy
        if (System.getProperty("com.ibm.vm.bitmode") == null)
            System.setProperty("com.ibm.vm.bitmode", "64");
    }

    // adapted from https://github.com/jline/jline3/blob/8bb13a89fad80e51726a29e4b1f8a0724fed78b2/terminal-jna/src/main/java/org/jline/terminal/impl/jna/win/JnaWinSysTerminal.java#L92-L96
    public static TerminalSize getSize() {
        long outputHandle = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        Kernel32.GetConsoleScreenBufferInfo(outputHandle, info);
        return TerminalSize.of(info.windowWidth(), info.windowHeight());
    }

    public static boolean setupAnsi() throws IOException {

        // from https://github.com/jline/jline3/blob/0660ae29f3af2ca3b56cdeca1530072306988e4d/terminal/src/main/java/org/jline/terminal/impl/AbstractWindowsTerminal.java#L53
        final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

        // adapted from https://github.com/jline/jline3/blob/1c850e16eccb7944abeb744db363968ad29f4fcf/terminal-jansi/src/main/java/org/jline/terminal/impl/jansi/win/JansiWinSysTerminal.java#L45-L50
        long console = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
        int[] mode = new int[1];
        if (Kernel32.GetConsoleMode(console, mode) == 0)
            throw new IOException("Failed to get console mode: " + Kernel32.getLastErrorMessage());
        return Kernel32.SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0;
    }

}
