ThisBuild / scalaVersion := "3.8.3"

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

val platform = {
  val os = System.getProperty("os.name").toLowerCase
  val arch = System.getProperty("os.arch")
  if (os.startsWith("linux"))
    if (arch == "aarch64") "linux-aarch64" else "linux"
  else if (os.startsWith("mac"))
    if (arch == "aarch64") "mac-aarch64" else "mac"
  else if (os.startsWith("windows")) "win"
  else throw new RuntimeException(s"Unknown OS: $os")
}

val javafxDeps = javafxModules.map { m =>
  "org.openjfx" % s"javafx-$m" % javafxVersion classifier platform
}

ThisBuild / libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.scalafx" %% "scalafx" % "26.0.0-R38",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.7.0"
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
    assembly / mainClass := Some(
      "scalatro.ScalatroMain"
    ),
    assembly / assemblyJarName := "scalatro.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", _*)            => MergeStrategy.first
      case "module-info.class"                 => MergeStrategy.discard
      case x => MergeStrategy.defaultMergeStrategy(x)
    }
  )

// See https://www.wartremover.org/doc/warts.html.
wartremoverErrors ++= Seq(
  Wart.AnyVal,
  Wart.EitherProjectionPartial,
  Wart.IterableOps,
  Wart.LeakingSealed,
  Wart.Null,
  Wart.ObjectThrowable,
  Wart.Option2Iterable,
  Wart.OptionPartial,
  Wart.PlatformDefault,
  Wart.Product,
  Wart.Return,
  Wart.Serializable,
  Wart.TryPartial
)

wartremoverWarnings ++= Seq(
  Wart.ArrayEquals,
  Wart.ArrayToString,
  Wart.AsInstanceOf,
  Wart.CollectHeadOption,
  Wart.DropTakeToSlice,
  Wart.FilterEmpty,
  Wart.FilterHeadOption,
  Wart.FilterSize,
  Wart.FindExists,
  Wart.ForeachEntry,
  Wart.GetGetOrElse,
  Wart.GetOrElseNull,
  Wart.IsInstanceOf,
  Wart.KeySet,
  Wart.MapContains,
  Wart.MapUnit,
  Wart.NonUnitStatements,
  Wart.ReverseFind,
  Wart.ReverseIterator,
  Wart.ReverseTakeReverse,
  Wart.SizeIs,
  Wart.SizeToLength,
  Wart.SortFilter,
  Wart.SortedMaxMin,
  Wart.SortedMaxMinOption,
  Wart.StringPlusAny,
  Wart.ToString
)
