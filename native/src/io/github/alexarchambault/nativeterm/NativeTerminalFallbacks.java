package io.github.alexarchambault.nativeterm;

import io.github.alexarchambault.isterminal.IsTerminal;
import io.github.alexarchambault.nativeterm.internal.ScriptRunner;
import io.github.alexarchambault.nativeterm.internal.WindowsTermScripts;

import java.io.IOException;

import static io.github.alexarchambault.nativeterm.NativeTerminal.isWindows;

public final class NativeTerminalFallbacks {

    private NativeTerminalFallbacks() {}

    public static TerminalSize getSize() throws InterruptedException, IOException {
        if (!IsTerminal.isTerminal())
            throw new IllegalArgumentException("Cannot get terminal size without a terminal");
        if (isWindows) {
            String output = ScriptRunner.runPowerShellScript(WindowsTermScripts.getConsoleDimScript).trim();
            String[] lines = output.split("\\r?\\n");
            String lastLine = lines[lines.length - 1];
            if (lastLine.startsWith("Error:"))
                throw new IOException(lastLine);
            if (lastLine.startsWith("Size: ")) {
                lastLine = lastLine.substring("Size: ".length());
                String[] split = lastLine.split("\\s+");
                return new TerminalSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
            throw new IOException("Invalid output from PowerShell script that gets terminal size: '" + output + "'");
        }
        else {
            return new TerminalSize(ScriptRunner.runTput("cols"), ScriptRunner.runTput("lines"));
        }
    }

    public static boolean setupAnsi() throws InterruptedException, IOException {
        if (isWindows && IsTerminal.isTerminal()) {
            String output = ScriptRunner.runPowerShellScript(WindowsTermScripts.enableAnsiScript);
            String[] lines = output.split("\\r?\\n");
            String lastLine = lines[lines.length - 1];
            return Boolean.parseBoolean(lastLine);
        }
        return true;
    }
}
