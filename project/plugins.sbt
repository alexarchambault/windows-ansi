addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.3")
addSbtPlugin(("io.github.alexarchambault.sbt" % "sbt-compatibility" % "0.0.6").exclude("com.typesafe", "sbt-mima-plugin"))
addSbtPlugin("com.github.alexarchambault.tmp" % "sbt-mima-plugin" % "0.7.1-SNAPSHOT")

resolvers += Resolver.sonatypeRepo("snapshots")
