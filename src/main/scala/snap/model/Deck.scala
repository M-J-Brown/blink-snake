package snap.model

sealed trait Suit
object Suit {
  case object Hearts extends Suit
  case object Diamonds extends Suit
  case object Clubs extends Suit
  case object Spades extends Suit
  val all: IndexedSeq[Suit] = IndexedSeq(Hearts, Diamonds, Clubs, Spades)
}

sealed trait Value
object Value {
  case object Ace extends Value
  case object Two extends Value
  case object Three extends Value
  case object Four extends Value
  case object Five extends Value
  case object Six extends Value
  case object Seven extends Value
  case object Eight extends Value
  case object Nine extends Value
  case object Ten extends Value
  case object Jack extends Value
  case object Queen extends Value
  case object King extends Value
  val all: IndexedSeq[Value] = IndexedSeq(
    Ace,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King
  )
}

case class Card(value: Value, suit: Suit)

object Deck {
  implicit class DeckOps(deck: Deck) {
    def cut: (Deck, Deck) = deck.splitAt(deck.size / 2)
  }
  type Deck = List[Card]

  val full: Deck =
    (for {
      suit <- Suit.all
      value <- Value.all
    } yield Card(value, suit)).toList
}
