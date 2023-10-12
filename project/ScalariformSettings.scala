import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Def
import scalariform.formatter.preferences._

object ScalariformSettings {
  lazy val scalariformSettings: Seq[Def.SettingsDefinition] = {
    Seq(
      ScalariformKeys.autoformat := true,
      ScalariformKeys.withBaseDirectory := true,
      // description of options found here -> https://github.com/scala-ide/scalariform
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignArguments, true)
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AllowParamGroupsOnNewlines, true)
        .setPreference(CompactControlReadability, false)
        .setPreference(CompactStringConcatenation, false)
        .setPreference(DanglingCloseParenthesis, Force)
        .setPreference(DoubleIndentConstructorArguments, true)
        .setPreference(DoubleIndentMethodDeclaration, true)
        .setPreference(FirstArgumentOnNewline, Force)
        .setPreference(FirstParameterOnNewline, Force)
        .setPreference(FormatXml, true)
        .setPreference(IndentLocalDefs, true)
        .setPreference(IndentPackageBlocks, true)
        .setPreference(IndentSpaces, 2)
        .setPreference(IndentWithTabs, false)
        .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
        .setPreference(NewlineAtEndOfFile, true)
        .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
        .setPreference(PreserveSpaceBeforeArguments, true)
        .setPreference(RewriteArrowSymbols, false)
        .setPreference(SpaceBeforeColon, false)
        .setPreference(SpaceBeforeContextColon, false)
        .setPreference(SpaceInsideBrackets, false)
        .setPreference(SpaceInsideParentheses, false)
        .setPreference(SpacesAroundMultiImports, false)
        .setPreference(SpacesWithinPatternBinders, true)
    )
  }
}
