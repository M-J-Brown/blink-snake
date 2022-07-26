package snap.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DeckSpec extends AnyWordSpec with Matchers {
  "A Deck" when {
    "full" should {
      "have 52 cards" in {
        Deck.full.size shouldBe 52
      }
    }
  }
}
