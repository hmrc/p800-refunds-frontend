import sbt.Keys._
import play.sbt.routes.RoutesKeys.routes
import sbt._
import wartremover.Wart
import wartremover.WartRemover.autoImport._

object WartRemoverSettings {

  val wartRemoverSettings =
    Seq(
      (Compile / compile / wartremoverErrors) ++= {
        if (StrictBuilding.strictBuilding.value) Warts.allBut(
          Wart.DefaultArguments,
          Wart.ImplicitConversion,
          Wart.ImplicitParameter,
          Wart.Nothing,
          Wart.Overloading,
          Wart.SizeIs,
          Wart.SortedMaxMinOption,
          Wart.Throw,
          Wart.ToString,
          Wart.PlatformDefault,
          Wart.Product,
          Wart.JavaSerializable,
          Wart.Serializable
        )
        else Nil
      },
      Test / compile / wartremoverErrors --= Seq(
        Wart.Any,
        Wart.Equals,
        Wart.GlobalExecutionContext,
        Wart.Null,
        Wart.NonUnitStatements,
        Wart.PublicInference
      ),
      wartremoverExcluded ++= (Compile / routes).value ++
        target.value.get // stops a weird wart remover Null error being thrown, we don't care about target directory
    )
}
