package snap.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.List

class GameSpec extends AnyWordSpec with Matchers {
  "A Game" when {
    val c1 = Card(Value.Ace, Suit.Spades)
    val c2 = Card(Value.Ace, Suit.Clubs)
    val c3 = Card(Value.Ace, Suit.Hearts)
    val c4 = Card(Value.Ace, Suit.Diamonds)
    val game = Game.InProgress(List(c1), List(c2), List(c3), List(c4))

    "started" should {
      "split the cards" in {
        Game.start(Deck.full) match {
          case Game.InProgress(p1, Nil, p2, Nil) =>
            p1 ::: p2 should contain theSameElementsAs Deck.full
          case _ => fail()
        }
      }
    }
    "played" should {
      "tick" in {
        game.playOne shouldBe Game.InProgress(List(), List(c1, c2), List(c3), List(c4))
        game.playTwo shouldBe Game.InProgress(List(c1), List(c2), List(), List(c3, c4))
      }
    }

    "snap check" should {
      "work" in {
        game.isSnap(MatchingMode.Value) shouldBe true
        game.isSnap(MatchingMode.Suit) shouldBe false
      }
      "cope with an empty stack" in {
        Game.InProgress(Nil, Nil, Nil, Nil).isSnap(MatchingMode.Value) shouldBe false
      }
    }

    "snapped" should {
      "claim the stacks if correct" in {
        game.trySnapOne(MatchingMode.Value) shouldBe Game.InProgress(List(c1, c2, c4), List(), List(c3), List())
        game.trySnapTwo(MatchingMode.Value) shouldBe Game.InProgress(List(c1), List(), List(c3, c4, c2), List())
      }
      "claim the stacks to the other player if incorrect" in {
        game.trySnapOne(MatchingMode.Suit) shouldBe Game.InProgress(List(c1), List(), List(c3, c4, c2), List())
        game.trySnapTwo(MatchingMode.Suit) shouldBe Game.InProgress(List(c1, c2, c4), List(), List(c3), List())
      }
    }
  }
}
