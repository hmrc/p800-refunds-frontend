import play.sbt.routes.RoutesKeys.routes
import sbt.Keys.compile
import sbt.{Compile, Test}
import wartremover.{Wart, Warts}
import wartremover.WartRemover.autoImport.{wartremoverErrors, wartremoverExcluded}

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
      wartremoverExcluded ++= (Compile / routes).value
    )
}
