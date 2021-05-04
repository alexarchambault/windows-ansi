package io.github.alexarchambault.windowsansi;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import java.io.IOException;

@TargetClass(className = "io.github.alexarchambault.windowsansi.WindowsAnsi")
@Platforms({Platform.DARWIN.class, Platform.LINUX.class})
public final class WindowsAnsiSubstitutions {

    @Substitute
    public static WindowsAnsi.Size terminalSize() {
        throw new RuntimeException("Not available on this platform");
    }

    @Substitute
    public static boolean setup() throws IOException {
        return false;
    }

}
