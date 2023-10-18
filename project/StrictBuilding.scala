import sbt.{Def, Global, SettingKey, settingKey}

object StrictBuilding {

  val strictBuilding: SettingKey[Boolean] = settingKey[Boolean](
    "If set to true, this enables strict compiler options, wartremover, fatal warnings, etc. During development, you can toggle it to false locally if you want to postpone code cleaning."
  )

  val strictBuildingSetting: Def.Setting[Boolean] = Global / StrictBuilding.strictBuilding := true

}
