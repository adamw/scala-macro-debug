import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := "com.softwaremill",
    version       := "0.0.1-SNAPSHOT",
    scalaVersion  := "2.10.0-RC3",
    scalacOptions += "",
    licenses      := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil
  )
}

object ScalaMacroDebugBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings
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
    settings = buildSettings
  ) dependsOn(macros)
}
