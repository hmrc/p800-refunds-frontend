/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testsupport.stubs

import cats.syntax.eq._
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.{RequestPatternBuilder, MatchResult, StringValuePattern}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{Format, Json}

import scala.jdk.CollectionConverters.MapHasAsJava

object WireMockHelpers {

  def verifyNone(url: String): Unit = verify(exactly(0), postRequestedFor(urlPathEqualTo(url)))

  def verifyGetExactlyWithHeader(url: String, requiredHeaders: Seq[(String, String)], count: Int): Unit = {
    val rpb: RequestPatternBuilder = requiredHeaders.foldLeft(getRequestedFor(urlPathEqualTo(url)))((acc, c) =>
      acc.andMatching((request: Request) =>
        customValueMatcherWithHeader(url, c._1, c._2, request)))

    verify(exactly(count), rpb)
  }

  private def customValueMatcherWithHeader(url: String, headerKey: String, headerValue: String, request: Request): MatchResult =
    MatchResult.of(request.getUrl === url && request.header(headerKey).containsValue(headerValue))

  /**
   * Useful wiremock helper to verify that the request body serialises to what we expect.
   * Hint: If it's not working and you can't work out why, check what A you are passing in... :)
   *
   * @param url    : String
   * @param format : play.api.libs.json.Format as an implicit
   * @tparam A : Model we want to get format for
   */
  def verifyWithBodyParse[A](url: String)(implicit format: Format[A]): Unit = verify(
    postRequestedFor(urlPathEqualTo(url))
      .andMatching((value: Request) => customValueMatcher(url, value))
  )

  def verifyExactlyWithBodyParse[A](url: String, count: Int)(implicit format: Format[A]): Unit = verify(
    exactly(count),
    postRequestedFor(urlPathEqualTo(url))
      .andMatching((value: Request) => customValueMatcher(url, value))
  )

  /**
   * Same as above, but it also compares a Json serialised expected A with the wiremock request
   */
  def verifyWithBodyParse[A](url: String, expected: A)(implicit format: Format[A]): Unit = verify(
    postRequestedFor(urlPathEqualTo(url))
      .andMatching((value: Request) => customValueMatcher(url, value))
      .withRequestBody(equalToJson(Json.toJson(expected).toString()))
  )

  /**
   * Same as above, but overloaded with headers to check
   */
  def verifyWithBodyParse[A](url: String, headers: (String, String))(implicit format: Format[A]): Unit = verify(
    postRequestedFor(urlPathEqualTo(url))
      .withHeader(headers._1, equalTo(headers._2))
      .andMatching((value: Request) => customValueMatcher(url, value))
  )

  /**
   * Same as above, but overloaded with headers to check and also compares a Json serialised expected A with the wiremock request
   */
  def verifyWithBodyParse[A](url: String, headers: (String, String), expected: A)(implicit format: Format[A]): Unit = verify(
    postRequestedFor(urlPathEqualTo(url))
      .withHeader(headers._1, equalTo(headers._2))
      .andMatching((value: Request) => customValueMatcher(url, value))
      .withRequestBody(equalToJson(Json.toJson(expected).toString()))
  )

  /**
   * Helper method to attempt to parse request body into type, using asOpt.
   * If option is empty, it can't and wiremock verify should fail, else wiremock verify is successful.
   */
  private def customValueMatcher[A](url: String, request: Request)(implicit format: Format[A]): MatchResult =
    MatchResult.of(request.getUrl === url && Json.parse(request.getBodyAsString).asOpt[A].nonEmpty)

  def stubForPostNoResponseBody(
      url:             String,
      responseStatus:  Int                               = Status.OK,
      requiredHeaders: Seq[(String, StringValuePattern)] = Nil
  ): StubMapping = stubFor(
    postMappingWithHeaders(url, requiredHeaders)
      .willReturn(
        aResponse()
          .withStatus(responseStatus)
      )
  )

  def stubForPost(
      url:             String,
      responseBody:    String,
      responseStatus:  Int                               = Status.OK,
      requestBodyJson: Option[String]                    = None,
      queryParams:     Map[String, StringValuePattern]   = Map.empty,
      requiredHeaders: Seq[(String, StringValuePattern)] = Nil
  ): StubMapping = {
    val mb: MappingBuilder = postMappingWithHeaders(url, requiredHeaders)

    stubFor(
      requestBodyJson.fold(mb)(requestBodyJson => mb.withRequestBody(equalToJson(requestBodyJson, true, true)))
        .withQueryParams(queryParams.asJava)
        .willReturn(
          aResponse()
            .withStatus(responseStatus)
            .withHeader("Content-Type", "application/json")
            .withBody(responseBody)
        )
    )
  }

  def stubForPostWithRequestBodyMatching(
      url:                 String,
      requestMatchingPath: String,
      jsonBody:            String,
      responseStatus:      Int                               = Status.OK,
      requiredHeaders:     Seq[(String, StringValuePattern)] = Nil
  ): StubMapping = stubFor(
    postMappingWithHeaders(url, requiredHeaders)
      .withRequestBody(matchingJsonPath(requestMatchingPath))
      .willReturn(
        aResponse()
          .withStatus(responseStatus)
          .withBody(jsonBody)
      )
  )

  private def postMappingWithHeaders(
      url:             String,
      requiredHeaders: Seq[(String, StringValuePattern)]
  ): MappingBuilder =
    requiredHeaders.foldLeft(post(urlPathEqualTo(url)))((acc, c) => acc.withHeader(c._1, c._2))

  def stubForGetWithResponseBody(
      url:             String,
      responseBody:    String,
      responseStatus:  Int                               = Status.OK,
      requiredHeaders: Seq[(String, StringValuePattern)] = Nil
  ): StubMapping = {

    val mb: MappingBuilder = requiredHeaders.foldLeft(get(urlPathEqualTo(url)))((acc, c) => acc.withHeader(c._1, c._2))
    stubFor(mb.willReturn(
      aResponse()
        .withStatus(responseStatus)
        .withBody(responseBody)
    ))
  }

}
