
lazy val microservice = Project("p800-refunds-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion        := 0,
    scalaVersion        := "2.13.8",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= ScalaCompilerFlags.scalaCompilerOptions,
    pipelineStages := Seq(gzip),
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(commands += SbtCommands.runTestOnlyCommand)
  .settings(ScalariformSettings.scalariformSettings: _*)
  .settings(SbtUpdatesSettings.sbtUpdatesSettings: _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(WartRemoverSettings.wartRemoverSettings)
  .settings(PlayKeys.playDefaultPort := 10150)
