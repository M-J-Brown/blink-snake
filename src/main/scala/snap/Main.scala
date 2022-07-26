package snap

import cats.effect.{IO, IOApp}
import cats.effect.std.Console
import cats.implicits._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = Console[IO].println("Ran my app")
}