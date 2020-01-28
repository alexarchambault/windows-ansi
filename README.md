
# windows-ansi

[![Build status](https://github.com/alexarchambault/windows-ansi/workflows/CI/badge.svg)](https://github.com/alexarchambault/windows-ansi/actions?query=workflow%3ACI)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alexarchambault.windows-ansi/windows-ansi.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.alexarchambault.windows-ansi/windows-ansi)

*windows-ansi* is a small Java library to setup / interact with a Windows terminal. It allows to
- query the terminal size, and
- change the console mode so that it accepts ANSI escape codes.

It relies on the [jansi](https://github.com/fusesource/jansi) library to do so, and also works from
GraalVM native images.

Compared to using [jline](https://github.com/jline/jline3), *windows-ansi* only and solely calls the right
`kernel32.dll` system calls (like [`SetConsoleMode`](https://docs.microsoft.com/en-us/windows/console/setconsolemode)
or [`GetConsoleScreenBufferInfo`](https://docs.microsoft.com/en-us/windows/console/getconsolescreenbufferinfo)), lowering the odds of something going wrong when generating or using a GraalVM native image for example.

## Usage

Add to your `build.sbt`
```scala
libraryDependencies += "io.github.alexarchambault.windows-ansi" % "windows-ansi" % "0.0.1"
```

The latest version is [![Maven Central](https://img.shields.io/maven-central/v/io.github.alexarchambault.windows-ansi/windows-ansi.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.alexarchambault.windows-ansi/windows-ansi).

The `WindowsAnsi` methods should only be called from Windows. You can check that
the current application is running on Windows like:
```java
boolean isWindows = System.getProperty("os.name")
        .toLowerCase(java.util.Locale.ROOT)
        .contains("windows");
```

When using GraalVM, the following should work too, and also has the benefit of simply
discarding one of the `if` branches at image generation time:
```java
// requires the org.graalvm.nativeimage:svm dependency,
// which can usually be marked as "provided"
if (com.oracle.svm.core.os.IsDefined.WIN32()) {
    // call io.github.alexarchambault.windowsansi.WindowsAnsi methods
} else {
    // not on Windows, handle things like you would on Unixes
}
```

### Change terminal mode

Change the terminal mode so that it accepts ANSI escape codes with
```java
import io.github.alexarchambault.windowsansi.WindowsAnsi;

boolean success = WindowsAnsi.setup();
```

A returned value of `false` means ANSI code aren't supported by the Windows version you're running on.
These are supposed to be supported by Windows 10 build 10586 (Nov. 2015) onwards.

### Get terminal size

```java
import io.github.alexarchambault.windowsansi.WindowsAnsi;

WindowsAnsi.Size size = WindowsAnsi.terminalSize();
int width = size.getWidth();
int height = size.getHeight();
```

## License

All files in this repository except `NativeImageFeature.java` can be used either under the
Apache 2.0 license, or the GNU GPL version 2 license, at your convenience.

The `NativeImageFeature.java` file, originally based on a GNU GPL version 2 only file, is licensed only
under the GNU GPL version 2 license.

