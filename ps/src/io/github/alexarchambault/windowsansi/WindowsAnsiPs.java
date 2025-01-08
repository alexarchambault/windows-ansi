package io.github.alexarchambault.windowsansi;

import java.io.IOException;

public final class WindowsAnsiPs {

    static boolean isWindows;

    static {
        isWindows = System.getProperty("os.name")
                .toLowerCase(java.util.Locale.ROOT)
                .contains("windows");
    }

    // adapted from https://github.com/rprichard/winpty/blob/7e59fe2d09adf0fa2aa606492e7ca98efbc5184e/misc/ConinMode.ps1
    static String script = "$signature = @'\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern IntPtr GetStdHandle(int nStdHandle);\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern uint GetConsoleMode(\n" +
            "    IntPtr hConsoleHandle,\n" +
            "    out uint lpMode);\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern uint SetConsoleMode(\n" +
            "    IntPtr hConsoleHandle,\n" +
            "    uint dwMode);\n" +
            "public const int STD_OUTPUT_HANDLE = -11;\n" +
            "public const int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;\n" +
            "'@\n" +
            "\n" +
            "$WinAPI = Add-Type -MemberDefinition $signature `\n" +
            "    -Name WinAPI -Namespace ConinModeScript `\n" +
            "    -PassThru\n" +
            "\n" +
            "$handle = $WinAPI::GetStdHandle($WinAPI::STD_OUTPUT_HANDLE)\n" +
            "$mode = 0\n" +
            "$ret = $WinAPI::GetConsoleMode($handle, [ref]$mode)\n" +
            "if ($ret -eq 0) {\n" +
            "    throw \"GetConsoleMode failed (is stdin a console?)\"\n" +
            "}\n" +
            "$ret = $WinAPI::SetConsoleMode($handle, $mode -bor $WinAPI::ENABLE_VIRTUAL_TERMINAL_PROCESSING)\n" +
            "if ($ret -eq 0) {\n" +
            "    throw \"SetConsoleMode failed (is stdin a console?)\"\n" +
            "}\n";

    // not sure what happens on Windows versions that don't support ANSI mode
    public static void setup() throws InterruptedException, IOException {
        if (isWindows && System.console() != null) {
            PowershellRunner.runScript(script);
        }
    }

}
