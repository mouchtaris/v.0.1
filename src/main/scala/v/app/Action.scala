package v
package app

trait Action extends redux.Command[State] {
  def apply(state: State): State
  def toString: String
}
