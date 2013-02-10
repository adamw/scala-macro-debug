import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := "com.softwaremill.scalamacrodebug",
    version       := "0.0.1-SNAPSHOT",
    scalaVersion  := "2.10.0",
    scalacOptions += "",
    licenses      := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    publishTo     <<= (version) { version: String =>
      val nexus = "https://nexus.softwaremill.com/content/repositories/"
      if (version.trim.endsWith("SNAPSHOT"))  Some("softwaremill-public-snapshots" at nexus + "snapshots/")
      else                                    Some("softwaremill-public-releases"  at nexus + "releases/")
    },
    credentials   += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    // For some reason generating the docs generates a publish error
    publishArtifact in (Compile, packageDoc) := false
  )
}

object ScalaMacroDebugBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(publishArtifact := false)
  ) aggregate(macros, examples)

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _))
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(publishArtifact := false)
  ) dependsOn(macros)
}
