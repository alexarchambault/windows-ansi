
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


lazy val jni = project
  .settings(
    shared,
    name := "windows-ansi",
    libraryDependencies ++= Seq(
      "org.fusesource.jansi" % "jansi" % "2.0.1",
      "org.graalvm.nativeimage" % "svm" % "20.2.0" % Provided
    )
  )

lazy val ps = project
  .settings(
    shared,
    name := "windows-ansi-ps",
  )

// root project
disablePlugins(MimaPlugin)
skip.in(publish) := true
shared
