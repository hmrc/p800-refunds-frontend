package uk.gov.hmrc.p800refundsfrontend.testsupport

import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

object TdRequest {
  private val authToken = "authorization-value"
  private val akamaiReputationValue = "akamai-reputation-value"
  private val requestId = "request-id-value"
  private val sessionId = "TestSession-4b87460d-6f43-4c4c-b810-d6f87c774854"
  private val trueClientIp = "client-ip"
  private val trueClientPort = "client-port"
  private val deviceId = "device-id"

  implicit class FakeRequestOps[T](r: FakeRequest[T]) {

    def withAuthToken(authToken: String = authToken): FakeRequest[T] = r.withSession((SessionKeys.authToken, authToken))

    def withAkamaiReputationHeader(akamaiReputatinoValue: String = akamaiReputationValue): FakeRequest[T] =
      r.withHeaders(HeaderNames.akamaiReputation -> akamaiReputatinoValue)

    def withRequestId(requestId: String = requestId): FakeRequest[T] = r.withHeaders(HeaderNames.xRequestId -> requestId)

    def withSessionId(sessionId: String = sessionId): FakeRequest[T] = r.withSession(SessionKeys.sessionId -> sessionId)

    def withTrueClientIp(ip: String = trueClientIp): FakeRequest[T] = r.withHeaders(HeaderNames.trueClientIp -> ip)

    def withTrueClientPort(port: String = trueClientPort): FakeRequest[T] = r.withHeaders(HeaderNames.trueClientPort -> port)

    def withDeviceId(deviceId: String = deviceId): FakeRequest[T] = r.withHeaders(HeaderNames.deviceID -> deviceId)
  }
}

