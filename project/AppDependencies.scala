import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.3.0"
  private val hmrcMongoVersion = "1.6.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "7.29.0-play-28",
    "com.beachape"      %% "enumeratum-play"            % "1.7.0", //later version results in JsBoolean error for case classes when being used with BsonDocs
    "org.typelevel"     %% "cats-core"                  % "2.10.0",
    "org.julienrf"      %% "play-json-derived-codecs"   % "7.0.0", //choose carefully
    "io.scalaland"      %% "chimney"                    % "0.8.4",
    "org.webjars"       %  "jquery"                     % "3.7.1",
    "org.webjars.npm"   %  "accessible-autocomplete"    % "2.0.4"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion exclude("com.github.tomakehurst", "wiremock-jre8"),
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % hmrcMongoVersion,
    "org.jsoup"               %  "jsoup"                      % "1.17.1",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "3.0.1",
    "org.scalatest"           %% "scalatest"                  % "3.2.17",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.62.2",
    "org.pegdown"             %  "pegdown"                    % "1.6.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % "4.16.1",
    "org.seleniumhq.selenium" %  "htmlunit-driver"            % "4.13.0",
    "org.wiremock"            %  "wiremock-standalone"        % "3.3.1",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"
  // format: ON
  ).map(_ % Test)
}
