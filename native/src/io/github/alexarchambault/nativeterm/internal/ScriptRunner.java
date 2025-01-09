package io.github.alexarchambault.nativeterm.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class ScriptRunner {

    public static String runPowerShellScript(String script) throws InterruptedException, IOException {

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

        Process proc = builder.start();

        StringBuilder results = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        try {
            String line = reader.readLine();
            while (line != null) {
                results.append(line);
                results.append("\r\n");
                line = reader.readLine();
            }
        } finally {
            proc.destroy();
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int retCode = proc.waitFor();
        if (retCode != 0)
            throw new IOException("Error running powershell script (return code: " + retCode + ")");
        return results.toString();
    }

    public static int runTput(String s) throws InterruptedException, IOException {
        Process proc = new ProcessBuilder()
                .command("tput", s)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        StringBuilder results = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        try {
            String line = reader.readLine();
            while (line != null) {
                results.append(line);
                results.append("\r\n");
                line = reader.readLine();
            }
        } finally {
            proc.destroy();
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int exitCode = proc.waitFor();
        if (exitCode != 0) throw new IOException("tput failed");
        return Integer.parseInt(results.toString().trim());
    }

}
