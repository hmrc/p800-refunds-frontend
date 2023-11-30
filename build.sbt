
val strictBuilding: SettingKey[Boolean] = StrictBuilding.strictBuilding //defining here so it can be set before running sbt like `sbt 'set Global / strictBuilding := true' ...`
StrictBuilding.strictBuildingSetting


lazy val microservice = Project("p800-refunds-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion        := 0,
    scalaVersion        := "2.13.8",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= ScalaCompilerFlags.scalaCompilerOptions,
    scalacOptions ++= {
          if (StrictBuilding.strictBuilding.value) ScalaCompilerFlags.strictScalaCompilerOptions else Nil
    },
    pipelineStages := Seq(gzip),
    Compile / scalacOptions -= "utf8",
    //TODO: talk to team about it. We can run tests in parallel, but we need unique journey ids in tests, otherwise tests becomes flaky very soon
    Test / parallelExecution := false
  )
  .settings(
      routesImport ++= Seq(
          "language.Language",
          "models.journeymodels.JourneyId"
      ))
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(commands ++= SbtCommands.commands)
  .settings(ScalariformSettings.scalariformSettings: _*)
  .settings(SbtUpdatesSettings.sbtUpdatesSettings: _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(WartRemoverSettings.wartRemoverSettings)
  .settings(PlayKeys.playDefaultPort := 10150)
