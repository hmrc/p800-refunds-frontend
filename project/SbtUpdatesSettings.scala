import com.timushev.sbt.updates.Compat.ModuleFilter
import com.timushev.sbt.updates.UpdatesKeys.dependencyUpdates
import com.timushev.sbt.updates.UpdatesPlugin.autoImport.{dependencyUpdatesFailBuild, dependencyUpdatesFilter, moduleFilterRemoveValue}
import sbt.Keys._
import sbt.{Def, _}
import xsbti.compile.CompileAnalysis

object SbtUpdatesSettings {

  lazy val sbtUpdatesSettings: Seq[Def.Setting[_ >: Boolean with Task[CompileAnalysis] with ModuleFilter]] = Seq(
    dependencyUpdatesFailBuild := StrictBuilding.strictBuilding.value,
    (Compile / compile) := ((Compile / compile) dependsOn dependencyUpdates).value,
    dependencyUpdatesFilter -= moduleFilter("org.scala-lang"),
    dependencyUpdatesFilter -= moduleFilter("com.typesafe.play"),
    dependencyUpdatesFilter -= moduleFilter("com.beachape", "enumeratum-play"), //problems with slf4j
    // locked to the version of play
    dependencyUpdatesFilter -= moduleFilter("org.julienrf", "play-json-derived-codecs"),
    dependencyUpdatesFilter -= moduleFilter("com.vladsch.flexmark", "flexmark-all"),
    dependencyUpdatesFilter -= moduleFilter("org.scalatestplus.play", "scalatestplus-play"),
    dependencyUpdatesFilter -= moduleFilter("com.beachape", "enumeratum-play"),
    //upgrading to version 9.0.0 breaks the javascript on the select your bank page - OPS-11918 to fix this
    dependencyUpdatesFilter -= moduleFilter("uk.gov.hmrc", "play-frontend-hmrc-play-30")

  )

}
