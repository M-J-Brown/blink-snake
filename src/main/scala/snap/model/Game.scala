package snap.model

import cats.implicits._
import Deck.DeckOps

sealed trait Player {
  final def other: Player = this match {
    case Player.One => Player.Two
    case Player.Two => Player.One
  }
}
object Player {
  case object One extends Player
  case object Two extends Player
}

/** All pure logic, just steps through a game of snap
  */
sealed trait Game
object Game {
  def start(deck: List[Card]): Game.InProgress = {
    val (playerOneDeck, playerTwoDeck) = deck.cut
    InProgress(playerOneDeck, Nil, playerTwoDeck, Nil)
  }
  case class InProgress(
      playerOneDeck: List[Card],
      playerOneStack: List[Card],
      playerTwoDeck: List[Card],
      playerTwoStack: List[Card]
  ) extends Game {
    def isSnap(matcher: (Card, Card) => Boolean): Boolean =
      (playerOneStack.headOption, playerTwoStack.headOption) match {
        case (Some(cardA), Some(cardB)) => matcher(cardA, cardB)
        case _                          => false
      }

    def trySnapOne(matcher: (Card, Card) => Boolean): Game =
      if (isSnap(matcher))
        copy(
          playerOneDeck = playerOneDeck ::: playerOneStack ::: playerTwoStack,
          playerOneStack = Nil,
          playerTwoStack = Nil
        )
      else
        copy(
          playerTwoDeck = playerTwoDeck ::: playerTwoStack ::: playerOneStack,
          playerOneStack = Nil,
          playerTwoStack = Nil
        )

    def trySnapTwo(matcher: (Card, Card) => Boolean): Game =
      if (isSnap(matcher))
        copy(
          playerTwoDeck = playerTwoDeck ::: playerTwoStack ::: playerOneStack,
          playerOneStack = Nil,
          playerTwoStack = Nil
        )
      else
        copy(
          playerOneDeck = playerOneDeck ::: playerOneStack ::: playerTwoStack,
          playerOneStack = Nil,
          playerTwoStack = Nil
        )

    def playOne: Game =
      if (playerTwoDeck.isEmpty) Finished(Player.Two)
      else
        playerOneDeck match {
          case head :: tail =>
            copy(playerOneDeck = tail, playerOneStack = head :: playerOneStack)
          case Nil =>
            Finished(Player.One) //Shouldn't happen unless there's a problem in the engine, but too fiddly to model out
        }

    def playTwo: Game =
      if (playerOneDeck.isEmpty) Finished(Player.One)
      else
        playerTwoDeck match {
          case head :: tail =>
            copy(playerTwoDeck = tail, playerTwoStack = head :: playerTwoStack)
          case Nil =>
            Finished(Player.Two) //Shouldn't happen unless there's a problem in the engine, but too fiddly to model out
        }
  }
  case class Finished(winner: Player) extends Game
}
