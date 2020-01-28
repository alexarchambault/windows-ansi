
inThisBuild(List(
  organization := "io.github.alexarchambault.windows-ansi",
  homepage := Some(url("https://github.com/alexarchambault/windows-ansi")),
  licenses := List("GPL-2.0" -> url("https://opensource.org/licenses/GPL-2.0")),
  developers := List(
    Developer(
      "alexarchambault",
      "Alexandre Archambault",
      "",
      url("https://github.com/alexarchambault")
    )
  )
))

lazy val shared = Def.settings(

  // publishing
  sonatypeProfileName := "io.github.alexarchambault",


  // pura Java
  crossPaths := false,
  autoScalaLibrary := false
)


lazy val core = project
  .settings(
    shared,
    name := "windows-ansi",
    libraryDependencies ++= Seq(
      "org.fusesource.jansi" % "jansi" % "1.18",
      "org.graalvm.nativeimage" % "svm" % "19.3.1" % Provided
    )
  )

lazy val `test-cli` = project
  .settings(
    shared,
    skip.in(publish) := true
  )

// root project
skip.in(publish) := true

