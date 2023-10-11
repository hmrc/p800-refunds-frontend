package uk.gov.hmrc.p800refundsfrontend.testsupport

import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.diagrams.Diagrams

object RichMatchers extends RichMatchers

trait RichMatchers
  extends Matchers
  with Diagrams
  with TryValues
  with EitherValues
  with OptionValues
  with AppendedClues
  with ScalaFutures
  with StreamlinedXml
  with Inside
  with Eventually
  with IntegrationPatience
