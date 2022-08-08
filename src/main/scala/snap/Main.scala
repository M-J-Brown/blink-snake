package snap

import cats.effect.std.{Console, Random}
import cats.effect.{IO, IOApp}
import cats.implicits._
import snap.engine.SnapEngine
import snap.model.{MatchingMode, Player}

import scala.concurrent.duration.DurationInt

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    _ <- Console[IO].println(s"Welcome to Snap!")
    _ <- Console[IO].println(s"How many decks of cards would you like to use?")
    decks <- getPositiveInt
    _ <- Console[IO].println(s"Would you like to play matching by [suit], [value], or [both]?")
    matchingMode <- getMatchingMode
    random <- Random.scalaUtilRandom[IO]
    _ <- SnapEngine.initialise(decks, matchingMode, random, 500.millis).use { case (outcome, input, ref) =>
      //TODO: Concurrently get an input and wait a "frame", then if the state has changed since the last "frame", print the new state
      ???
    }
  } yield ()

  def getPositiveInt: IO[Int] =
    Console[IO].readLine.flatMap(_.toIntOption match {
      case Some(value) if value > 0 => value.pure[IO]
      case _                        => Console[IO].println(s"That isn't a positive integer!") *> getPositiveInt
    })

  def getMatchingMode: IO[MatchingMode] =
    Console[IO].readLine.flatMap(MatchingMode.parse(_) match {
      case Some(value) => value.pure[IO]
      case None        => Console[IO].println(s"That isn't a mode!") *> getMatchingMode
    })

  def getInput: IO[Player] =
    Console[IO].readLine.flatMap {
      case "snap" => Player.One.pure[IO]
      case _      => getInput
    }
}
