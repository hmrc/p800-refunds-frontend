import sbt.Command

object SbtCommands {
  val runTestOnlyCommand: Command = Command.command("runTestOnly") { state =>
    state.globalLogging.full.info("running play using 'testOnlyDoNotUseInAppConf' routes...")
    s"""set javaOptions += "-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"""" ::
      "run" ::
      s"""set javaOptions -= "-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"""" ::
      state
  }

  /**
   * This disables strict compiler options,
   * such as wartremover, fatal warnings, and more.
   * Use it during development you wish to postpone code cleaning.
   */
  val relax: Command = Command.command(
    "relax"
  ) { state =>
      state.globalLogging.full.info("Ok, I'll turn a blind eye to some shortcomings. Remember, I won't be so lenient on Jenkins!")
      s"""set Global / strictBuilding := false"""
      state
    }

  val strictBuilding: Command = Command.command("strictBuilding") { state =>
    state.globalLogging.full.info("Turning on strict building")
    s"""set Global / strictBuilding := true"""
    state
  }

  val commands: Seq[Command] = Seq(
    runTestOnlyCommand,
    relax,
    strictBuilding
  )
}
