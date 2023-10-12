object ScalaCompilerFlags {
  lazy val scalaCompilerOptions: Seq[String] = Seq(
    "-Xfatal-warnings",
    "-Xlint:-missing-interpolator,_",
    "-Xlint:adapted-args",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    // required in place of silencer plugin
    "-Wconf:cat=unused-imports&src=html/.*:s",
    "-Wconf:src=routes/.*:s"
  )

}
