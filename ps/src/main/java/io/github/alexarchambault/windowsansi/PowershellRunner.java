package io.github.alexarchambault.windowsansi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class PowershellRunner {

    public static void runScript(String script) throws InterruptedException, IOException {

        String fullScript = "& {\n" +
                script +
                "\n}";

        Base64.Encoder base64 = Base64.getEncoder();
        String encodedScript = base64.encodeToString(fullScript.getBytes(StandardCharsets.UTF_16LE));

        ProcessBuilder builder = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-NonInteractive",
                "-EncodedCommand", encodedScript);
        builder.inheritIO();

        int retCode = builder.start().waitFor();
        if (retCode != 0)
            throw new IOException("Error running powershell script (return code: " + retCode + ")");
    }

}
