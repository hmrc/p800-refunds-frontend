import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.5.0"
  private val hmrcMongoVersion = "1.8.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "8.5.0",
    "com.beachape"      %% "enumeratum-play"            % "1.8.0", //later version results in JsBoolean error for case classes when being used with BsonDocs
    "org.typelevel"     %% "cats-core"                  % "2.10.0",
    /*
     * WARN! Choose this version carefully.
     * play-json-derived-codecs-10.1.0 was compiled for play 2.9,
     * whereas this project depends on play-3.0
     * This resulted in problems when running tests from Intellij Idea.
     * To workaround compatibility issues, play related transitive dependencies
     * were excluded from this library.
     * Once below PR is merged, there should be release a newer version
     * of this dependency compatible with play-3.0
     * https://github.com/julienrf/play-json-derived-codecs/pull/94
     */
    "org.julienrf"      %% "play-json-derived-codecs"   % "10.1.0" excludeAll(ExclusionRule().withOrganization("com.typesafe.play")),
    "io.scalaland"      %% "chimney"                    % "0.8.5",
    "org.webjars"       %  "jquery"                     % "3.7.1",
    "org.webjars.npm"   %  "accessible-autocomplete"    % "2.0.4"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion,
    "org.seleniumhq.selenium" %  "selenium-java"              % "4.19.1",
    "org.seleniumhq.selenium" %  "htmlunit-driver"            % "4.13.0"
  // format: ON
  ).map(_ % Test)
}
