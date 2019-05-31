lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "V dot zero dot one",
    scalaVersion := "2.12.8",
    mainClass in Compile := Some("v.Main"),
    // Scala JS
    //
    scalaJSUseMainModuleInitializer := true,
    //scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
  )