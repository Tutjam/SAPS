package models

import play.api.libs.json.{Json, Reads}
import utils.ThreatType.ThreatType

case class PhishingDetectorResponse(
                                     isSafe: Boolean,
                                     threatType: ThreatType
                                   )
object PhishingDetectorResponse {
  implicit val reads: Reads[PhishingDetectorResponse] = Json.reads[PhishingDetectorResponse]
}
