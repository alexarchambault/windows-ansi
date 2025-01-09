import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.1`
import $ivy.`com.github.lolgab::mill-mima::0.1.1`

import com.github.lolgab.mill.mima.Mima
import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.scalalib._
import mill.scalalib.publish._

trait WindowsAnsiPublishModule extends PublishModule with Mima {
  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "io.github.alexarchambault.native-terminal",
    url = "https://github.com/alexarchambault/native-terminal",
    licenses = Seq(
      License.`Apache-2.0`,
      License.`GPL-2.0-with-classpath-exception`
    ),
    versionControl = VersionControl.github("alexarchambault", "native-terminal"),
    developers = Seq(
      Developer("alexarchambault", "Alex Archambault", "https://github.com/alexarchambault")
    )
  )
  def publishVersion = T {
    val value = VcsVersion.vcsState().format()
    if (value.contains("-")) {
      val value0 = value.takeWhile(_ != '-')
      val lastDotIdx = value0.lastIndexOf('.')
      if (lastDotIdx < 0) value0 + "-SNAPSHOT"
      else
        value0.drop(lastDotIdx + 1).toIntOption match {
          case Some(lastNum) =>
            val prefix = value0.take(lastDotIdx)
            s"$prefix.${lastNum + 1}-SNAPSHOT"
          case None =>
            value0 + "-SNAPSHOT"
        }
    }
    else value
  }

  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "8"
  )

  def mimaPreviousVersions: T[Seq[String]] = T.input {
    val current = os.proc("git", "describe", "--tags", "--match", "v*")
      .call()
      .out.trim()
    os.proc("git", "tag", "-l")
      .call()
      .out.lines()
      .filter(_ != current)
      .filter(_.startsWith("v"))
      .filter(!_.contains("-"))
      .map(_.stripPrefix("v"))
      .map(coursier.core.Version(_))
      .sorted
      .map(_.repr)
  }
}

object native extends JavaModule with WindowsAnsiPublishModule {
  def artifactName = "native-terminal"
  def ivyDeps = Agg(
    ivy"io.github.alexarchambault:is-terminal:0.1.1",
    ivy"org.fusesource.jansi:jansi:2.4.1"
  )
}

object `native-graalvm` extends JavaModule with WindowsAnsiPublishModule {
  def moduleDeps = Seq(native)
  def artifactName = "native-terminal-graalvm"
  def pomSettings = super.pomSettings().copy(
    licenses = Seq(License.`GPL-2.0-with-classpath-exception`)
  )
  def compileIvyDeps = Agg(
    ivy"org.graalvm.nativeimage:svm:22.0.0.2"
  )
  def mimaPreviousVersions = {
    val publishedSince = coursier.core.Version("0.0.4")
    super.mimaPreviousVersions().dropWhile { v =>
      coursier.core.Version(v) < publishedSince
    }
  }
}

object fallbacks extends JavaModule with WindowsAnsiPublishModule {
  def artifactName = "native-terminal-fallbacks"
  def mimaPreviousVersions = {
    val publishedSince = coursier.core.Version("0.0.2")
    super.mimaPreviousVersions().dropWhile { v =>
      coursier.core.Version(v) < publishedSince
    }
  }
}
