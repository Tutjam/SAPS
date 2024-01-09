package models

import play.api.libs.json.{Json, Reads}
import utils.ThreatType.ThreatType

/**
 * Model reprezentujący odpowiedź z zewnętrznego serwisu służącego do weryfikacji niebezpiecznych URLi
 *
 * @param isSafe     określa czy jest bezpieczny
 * @param threatType określa typ zagrożenia
 */
case class PhishingDetectorResponse(
                                     isSafe: Boolean,
                                     threatType: Option[ThreatType]
                                   )

object PhishingDetectorResponse {
  implicit val reads: Reads[PhishingDetectorResponse] = Json.reads[PhishingDetectorResponse]
}
