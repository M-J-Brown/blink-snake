package snap.model

sealed trait MatchingMode extends ((Card, Card) => Boolean)
object MatchingMode {
  case object Suit extends MatchingMode {
    override def apply(v1: Card, v2: Card): Boolean = v1.suit == v2.suit
  }
  case object Value extends MatchingMode {
    override def apply(v1: Card, v2: Card): Boolean = v1.value == v2.value
  }
  case object Both extends MatchingMode {
    override def apply(v1: Card, v2: Card): Boolean =
      Suit(v1, v2) || Value(v1, v2)
  }
  def parse(string: String): Option[MatchingMode] = string.toLowerCase match {
    case "suit"  => Some(Suit)
    case "value" => Some(Value)
    case "both"  => Some(Both)
    case _       => None
  }
}
