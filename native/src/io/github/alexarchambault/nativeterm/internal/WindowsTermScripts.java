package io.github.alexarchambault.nativeterm.internal;

import java.io.IOException;

public final class WindowsTermScripts {

    static boolean isWindows;

    static {
        isWindows = System.getProperty("os.name")
                .toLowerCase(java.util.Locale.ROOT)
                .contains("windows");
    }

    // adapted from https://github.com/rprichard/winpty/blob/7e59fe2d09adf0fa2aa606492e7ca98efbc5184e/misc/ConinMode.ps1
    public static String enableAnsiScript =
            "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8\n" +
            "$signature = @'\n" +
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

    public static String getConsoleDimScript =
            "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8\n" +
            "$signature = @\"\n" +
            "using System;\n" +
            "using System.Runtime.InteropServices;\n" +
            "\n" +
            "public class Kernel32\n" +
            "{\n" +
            "    [DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "    public static extern bool GetConsoleScreenBufferInfo(IntPtr hConsoleOutput, out CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);\n" +
            "\n" +
            "    [DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "    public static extern IntPtr GetStdHandle(int nStdHandle);\n" +
            "\n" +
            "    public const int STD_OUTPUT_HANDLE = -11;\n" +
            "\n" +
            "    [StructLayout(LayoutKind.Sequential)]\n" +
            "    public struct COORD\n" +
            "    {\n" +
            "        public short X;\n" +
            "        public short Y;\n" +
            "    }\n" +
            "\n" +
            "    [StructLayout(LayoutKind.Sequential)]\n" +
            "    public struct SMALL_RECT\n" +
            "    {\n" +
            "        public short Left;\n" +
            "        public short Top;\n" +
            "        public short Right;\n" +
            "        public short Bottom;\n" +
            "    }\n" +
            "\n" +
            "    [StructLayout(LayoutKind.Sequential)]\n" +
            "    public struct CONSOLE_SCREEN_BUFFER_INFO\n" +
            "    {\n" +
            "        public COORD dwSize;\n" +
            "        public COORD dwCursorPosition;\n" +
            "        public short wAttributes;\n" +
            "        public SMALL_RECT srWindow;\n" +
            "        public COORD dwMaximumWindowSize;\n" +
            "    }\n" +
            "}\n" +
            "\"@\n" +
            "\n" +
            "Add-Type -TypeDefinition $signature -Language CSharp\n" +
            "\n" +
            "$outputHandle = [Kernel32]::GetStdHandle([Kernel32]::STD_OUTPUT_HANDLE)\n" +
            "\n" +
            "$info = New-Object Kernel32+CONSOLE_SCREEN_BUFFER_INFO\n" +
            "\n" +
            "if ([Kernel32]::GetConsoleScreenBufferInfo($outputHandle, [ref]$info)) {\n" +
            "    Write-Host \"Size: $($info.srWindow.Right - $info.srWindow.Left + 1) $($info.srWindow.Bottom - $info.srWindow.Top + 1)\"\n" +
            "} else {\n" +
            "    Write-Host \"Error: \" + [System.Runtime.InteropServices.Marshal]::GetLastWin32Error()\n" +
            "}\n";

}
