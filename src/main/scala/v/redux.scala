package v

import scala.collection.mutable

object redux {

  trait View[State, Sub] extends Any {
    def apply(state: State): Sub
    def apply(state: State, sub: Sub): State
  }

  final type Reducer[Action, State] = PartialFunction[Action, State ⇒ State]

  def zero[Action, State]: Reducer[Action, State] = { case _ ⇒ identity[State] }

  final case class Store[Action, State](
    state0: State,
    reducers: Seq[redux.Reducer[Action, State]] = Seq.empty,
  ) {
    type This = Store[Action, State]

    private[this] val new_states = mutable.Buffer.empty[State]

    def state: State =
      new_states.lastOption.getOrElse(state0)

    def states: Seq[State] =
      state0 +: new_states

    val reducer: Reducer[Action, State] =
      reducers
        .foldRight(zero[Action, State]) { (acc, reducer) ⇒
          {
            case action if reducer.isDefinedAt(action) ⇒
              reducer(action) andThen acc(action) // inherit acc's definedness
            case action ⇒
              acc(action)
          }
        }

    def dispatch(action: Action): Unit =
      Trace(s"dispatch:${action.toString}") {
        reducer
          .andThen { mod ⇒ Trace(s"mod:$mod") { mod(state) } }
          .andThen { state ⇒ Trace("appending state") { new_states append state } }
          .andThen { _ ⇒ println(s"dispatched $action: states: ${states.size}") }
          .apply(action)
      }

    def register(reducer: Reducer[Action, State]): Store[Action, State] =
      copy(reducers = reducers :+ reducer)
  }

  trait Command[State] extends Any {
    def apply(state: State): State
  }

  final case class reduce_commands[Action, State]() extends Reducer[Action, State] {
    override def isDefinedAt(x: Action): Boolean =
      x.isInstanceOf[Command[State]]

    override def apply(x: Action): State ⇒ State =
      if (!isDefinedAt(x))
        throw new MatchError(x)
      else
        x.asInstanceOf[Command[State]].apply
  }
}
