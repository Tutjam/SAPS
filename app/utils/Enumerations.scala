package utils

import play.api.libs.json.{Reads, Writes}


/**
 * Typy zagrożenia
 */
object ThreatType extends Enumeration {
  type ThreatType = Value

  val PHISHING = Value("PHISHING")
  val MALWARE = Value("MALWARE")
  val SOCIAL_ENGINEERING = Value("SOCIAL_ENGINEERING")

  implicit val reads: Reads[ThreatType] = Reads.enumNameReads(ThreatType)
  implicit val writes: Writes[ThreatType] = Writes.enumNameWrites
}
