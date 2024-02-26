import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  private val hmrcMongoVersion = "1.7.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "http-verbs-play-30"         % "14.12.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "8.5.0",
    "com.beachape"      %% "enumeratum-play"            % "1.8.0", //later version results in JsBoolean error for case classes when being used with BsonDocs
    "org.typelevel"     %% "cats-core"                  % "2.10.0",
    "org.julienrf"      %% "play-json-derived-codecs"   % "10.1.0", //choose carefully
    "io.scalaland"      %% "chimney"                    % "0.8.5",
    "org.webjars"       %  "jquery"                     % "3.7.1",
    "org.webjars.npm"   %  "accessible-autocomplete"    % "2.0.4"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion exclude("com.github.tomakehurst", "wiremock-jre8"),
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion,
    "org.jsoup"               %  "jsoup"                      % "1.17.2",
    "org.pegdown"             %  "pegdown"                    % "1.6.0",
    "org.seleniumhq.selenium" %  "selenium-java"              % "4.18.1",
    "org.seleniumhq.selenium" %  "htmlunit-driver"            % "4.13.0",
    "org.wiremock"            %  "wiremock-standalone"        % "3.4.2",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"
  // format: ON
  ).map(_ % Test)
}
