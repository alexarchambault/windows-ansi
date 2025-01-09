# native-terminal

[![Build status](https://github.com/alexarchambault/native-terminal/workflows/CI/badge.svg)](https://github.com/alexarchambault/native-terminal/actions?query=workflow%3ACI)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alexarchambault.native-terminal/native-terminal.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.alexarchambault.native-terminal/native-terminal)

*native-terminal* is a small Java library to setup / interact with a terminals in a native fashion. It allows to
- query the terminal size, and
- on Windows, change the console mode so that it accepts ANSI escape codes.

It relies on internals of the [jansi](https://github.com/fusesource/jansi) library to do so, and also works from
GraalVM native images.

Compared to using [jline](https://github.com/jline/jline3), *native-terminal* only and solely calls the right
`ioctl` system calls
(or `kernel32.dll` system calls, like [`SetConsoleMode`](https://docs.microsoft.com/en-us/windows/console/setconsolemode)
or [`GetConsoleScreenBufferInfo`](https://docs.microsoft.com/en-us/windows/console/getconsolescreenbufferinfo), on Windows),
lowering the odds of something going wrong when generating or using a GraalVM native image for example.

## Usage

Add to your `build.sbt`
```scala
libraryDependencies += "io.github.alexarchambault.native-terminal" % "native-terminal" % "0.0.7"
```

The latest version is [![Maven Central](https://img.shields.io/maven-central/v/io.github.alexarchambault.native-terminal/native-terminal.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.alexarchambault.native-terminal/native-terminal).

### Change terminal mode

Change the terminal mode on Windows, so that it accepts ANSI escape codes with
```java
import io.github.alexarchambault.nativeterm.NativeTerminal;

boolean success = NativeTerminal.setupAnsi();
```

A returned value of `false` means ANSI escape codes aren't supported by the Windows version you're running on.
These are supposed to be supported by Windows 10 build 10586 (Nov. 2015) onwards.

Calling this method is safe on other platforms. It simply returns true in that case.

### Get terminal size

```java
import io.github.alexarchambault.nativeterm.NativeTerminal;
import io.github.alexarchambault.nativeterm.TerminalSize;

TerminalSize size = NativeTerminal.size();
int width = size.getWidth();
int height = size.getHeight();
```

## License

All files in this repository, except `NativeImageFeature.java`, can be used either under the
Apache 2.0 license, or the GNU GPL version 2 license, at your convenience.

The `NativeImageFeature.java` file, originally based on a GNU GPL version 2 only file, is licensed only
under the GNU GPL version 2 license.

