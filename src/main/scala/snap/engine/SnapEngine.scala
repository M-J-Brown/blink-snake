package snap.engine

import cats.effect.kernel.Outcome
import cats.effect.std.{Queue, Random}
import cats.effect.{Async, Ref, Resource, Temporal}
import cats.implicits._
import snap.model._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/** Holds a game and a source of randomness.
  * Steps through the game every X, also receives events.
  * Basically want a metered stream handing out updates,
  */
case class SnapEngine[F[_]: Async](
    gameRef: Ref[F, Game.InProgress],
    input: Queue[F, GameEvent],
    matchingMode: MatchingMode,
    nextToPlay: Ref[F, Player],
    tickTime: FiniteDuration
) {
  private def getNextGame(
      event: GameEvent,
      nextToPlay: Player,
      game: Game.InProgress
  ): Game = event match {
    case GameEvent.Snap(Player.One) => game.trySnapOne(matchingMode)
    case GameEvent.Snap(Player.Two) => game.trySnapTwo(matchingMode)
    case GameEvent.Tick =>
      nextToPlay match {
        case Player.One => game.playOne
        case Player.Two => game.playTwo
      }
  }

  /** Returns the winner (eventually)
    */
  def play: F[Player] = {
    val action = Async[F].race(
      input.take,
      Temporal[F].sleep(tickTime).as(GameEvent.Tick)
    )
    (for {
      game <- gameRef.get
      nextAction <- action.map(_.merge)
      next <- nextToPlay.get
      nextGame = getNextGame(nextAction, next, game)
    } yield nextGame match {
      case ip: Game.InProgress =>
        gameRef.set(ip) *> nextToPlay.update(_.other) *> play
      case Game.Finished(winner) => winner.pure[F]
    }).flatten
  }
}

object SnapEngine {

  /** Returns:
    * - An action where you can wait for the game to finish, which returns the winner
    * - A queue you can post game actions to
    * - A ref containing the game at any given point
    */
  def initialise[F[_]: Async](
      decks: Int,
      matchingMode: MatchingMode,
      random: Random[F],
      tickTime: FiniteDuration
  ): Resource[
    F,
    (
        F[Outcome[F, Throwable, Player]],
        Queue[F, GameEvent],
        Ref[F, Game.InProgress]
    )
  ] = {
    assert(decks > 0)
    val cards = (for (_ <- 1 to decks) yield Deck.full).flatten
    val engine = for {
      shuffled <- random.shuffleList(cards.toList)
      gameRef <- Ref.of[F, Game.InProgress](Game.start(shuffled))
      nextToPlayRef <- Ref.of[F, Player](Player.One)
      input <- Queue.dropping[F, GameEvent](1)
      engine = SnapEngine(gameRef, input, matchingMode, nextToPlayRef, tickTime)
    } yield engine
    Resource
      .eval(engine)
      .flatMap(e =>
        Async[F].background(e.play).map(res => (res, e.input, e.gameRef))
      )
  }
}
