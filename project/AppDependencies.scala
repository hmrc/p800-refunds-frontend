import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.22.0"
  private val hmrcMongoVersion = "1.3.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "7.23.0-play-28",
    "com.beachape"      %% "enumeratum-play"            % "1.7.3"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % hmrcMongoVersion,
    "org.jsoup"               %  "jsoup"                      % "1.16.1",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "3.0.1",
    "org.scalatest"           %% "scalatest"                  % "3.2.17",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.62.2",
    "org.pegdown"             %  "pegdown"                    % "1.6.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % "4.14.0",
    "org.seleniumhq.selenium" %  "htmlunit-driver"            % "4.13.0",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "3.0.1",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"
  // format: ON
  ).map(_ % Test)
}
