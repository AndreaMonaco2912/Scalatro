ThisBuild / scalaVersion := "3.8.4"

ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-explain",
  "-new-syntax",
  "-encoding",
  "utf8",
  "-java-output-version",
  "25"
)

val javafxVersion = "26.0.1"
val javafxModules = Seq("base", "controls", "fxml", "graphics")

val javafxPlatforms = Seq("linux", "mac", "win")

val javafxDeps = for {
  m <- javafxModules
  p <- javafxPlatforms
} yield "org.openjfx" % s"javafx-$m" % javafxVersion classifier p

ThisBuild / libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "26.0.0-R38",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.7.0",
  "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1",
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.scalamock" %% "scalamock" % "7.5.5" % Test,
  "org.scalamock" %% "scalamock-cats-effect" % "7.5.5" % Test
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
    Compile / mainClass := Some("scalatro.Main"),
    assembly / mainClass := Some("scalatro.Main"),
    assembly / assemblyJarName := "scalatro.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", _*)            => MergeStrategy.first
      case "module-info.class"       => MergeStrategy.discard
      case x if x.endsWith(".class") => MergeStrategy.first
      case x if x.endsWith(".bss")   => MergeStrategy.first
      case x if x.endsWith(".jar")   => MergeStrategy.first
      case x => MergeStrategy.defaultMergeStrategy(x)
    }
  )
