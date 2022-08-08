package snap.model

sealed trait GameEvent
object GameEvent {
  case object Tick extends GameEvent
  case class Snap(player: Player) extends GameEvent
}
