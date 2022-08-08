package snap.engine

import cats.effect.IO
import cats.effect.std.Random
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import snap.model.{GameEvent, MatchingMode, Player}

import scala.concurrent.duration.DurationInt

/** TODO: Clock Test Kit
  */
class SnapEngineSpec extends AnyWordSpec with Matchers {
  "The Engine" should {
    "run to completion" in {
      val random = Random.scalaUtilRandomSeedInt[IO](1).unsafeRunSync()
      SnapEngine
        .initialise(1, MatchingMode.Suit, random, 1.millis)
        .use { case (outcome, _, ref) =>
          for {
            result <- outcome
            winner <- result.embed(
              IO.raiseError(new RuntimeException(s"Cancelled!"))
            )
            finalGameState <- ref.get
          } yield {
            winner shouldBe Player.One
            finalGameState.playerOneDeck.size shouldBe 0
            finalGameState.playerOneStack.size shouldBe 26
            finalGameState.playerTwoDeck.size shouldBe 1
            finalGameState.playerTwoStack.size shouldBe 25
          }
        }
        .unsafeRunSync()
    }

    "accept input" in {
      val random = Random.scalaUtilRandomSeedInt[IO](1).unsafeRunSync()
      SnapEngine
        .initialise(1, MatchingMode.Suit, random, 1000.millis)
        .use { case (outcome, input, ref) =>
          for {
            _ <- IO.sleep(2500.millis)
            _ <- input.offer(GameEvent.Snap(Player.Two))
            _ <- IO.sleep(500.millis)
            gameState <- ref.get
          } yield {
            gameState.playerOneDeck.size shouldBe 25
            gameState.playerOneStack.size shouldBe 0
            gameState.playerTwoDeck.size shouldBe 27
            gameState.playerTwoStack.size shouldBe 0
          }
        }
        .unsafeRunSync()
    }
  }
}
