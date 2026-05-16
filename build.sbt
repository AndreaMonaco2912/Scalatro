ThisBuild / scalaVersion := "3.8.3"

ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-explain",
  "-new-syntax",
  "-encoding", "utf8",
  "-java-output-version", "25"
)

val javafxVersion = "26.0.1"
val javafxModules = Seq("base", "controls", "fxml", "graphics")

val platform = {
  val os = System.getProperty("os.name").toLowerCase
  val arch = System.getProperty("os.arch")
  if      (os.startsWith("linux"))   if (arch == "aarch64") "linux-aarch64" else "linux"
  else if (os.startsWith("mac"))     if (arch == "aarch64") "mac-aarch64"   else "mac"
  else if (os.startsWith("windows")) "win"
  else throw new RuntimeException(s"Unknown OS: $os")
}

val javafxDeps = javafxModules.map { m =>
  "org.openjfx" % s"javafx-$m" % javafxVersion classifier platform
}

ThisBuild / libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "Scalatro",
    idePackagePrefix := Some("scalatro"),
    libraryDependencies ++= javafxDeps,
    fork := true,

    assembly / packageOptions += Package.ManifestAttributes(
      "Enable-Native-Access" -> "ALL-UNNAMED"
    ),
    assembly / mainClass := Some("scalatro.ScalatroMain"),  // ← your launcher class
    assembly / assemblyJarName := "scalatro.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", _*) => MergeStrategy.first
      case "module-info.class" => MergeStrategy.discard
      case x => MergeStrategy.defaultMergeStrategy(x)
    }
  )
