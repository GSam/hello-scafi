package examples

import it.unibo.scafi.incarnations.BasicAbstractIncarnation

// 1. Define (or import) an incarnation, which provides an instantiation of types and other classes to import
// Note: mix StandardLibrary into the incarnation to enable use of standard library traits
object MyIncarnation extends BasicAbstractIncarnation

// 2. Bring into scope the stuff from the chosen incarnation
import examples.MyIncarnation._

// 3. Define an "aggregate program" using the ScaFi DSL by extending AggregateProgram and specifying a "main" expression
class GradientProgram extends AggregateProgram {
  // Main program expression driving the ensemble
  // This is run in a loop for each agent
  // According to this expression, coordination messages are automatically generated
  // The platform/middleware/simulator is responsible for coordination
  override def main() = gradient(isSource)

  // The gradient is the (self-adaptive) field of the minimum distances from source nodes
  // `rep` is the construct for state transformation (remember the round-by-round loop behaviour)
  // `mux` is a purely functional multiplexer (selects the first or second branch according to condition)
  // `foldhoodPlus` folds over the neighbourhood (think like Scala's fold)
  // (`Plus` means "without self"--with plain `foldhood`, the device itself is folded)
  // `nbr(e)` denotes the values to be locally computed and shared with neighbours
  // `nbrRange` is a sensor that, when folding, returns the distance wrt each neighbour
  def gradient(source: Boolean): Double =
    rep(Double.PositiveInfinity){ distance =>
      mux(source) { 0.0 } {
        foldhoodPlus(Double.PositiveInfinity)(Math.min)(nbr{distance}+nbrRange)
      }
    }

  // A custom local sensor
  def isSource = sense[Boolean]("sens1")
  // A custom "neighbouring sensor"
  def nbrRange = nbrvar[Double]("NBRRANGE")
}

// 4. In your program, implement an "execution loop" whereby your device or system executes the aggregate program
/*object HelloScafi extends App {
  val program = new GradientProgram()
  // Import standard sensors name define in incarnation
  val sensorsNames = new StandardSensorNames {}
  import sensorsNames._
  // Now let's build a simplified system with sequential execution just to illustrate the execution model
  // Suppose the following topology: [1] -- [2] -- [3] -- [4] -- [5]
  // And that the source of the gradient is the device no. 2
  // Then, the expected result once the gradient has stabilised is: {1 -> 1, 2 -> 0, 3 -> 1, 4 -> 2, 5 -> 3}
  case class DeviceState(
      self: ID,
      exports: Map[ID, EXPORT],
      localSensors: Map[CNAME, Any],
      nbrSensors: Map[CNAME, Map[ID, Any]]
  )
  val devices = 1 to 5
  var state: Map[ID, DeviceState] = (for {
    d <- devices
    nbrs = Seq(d - 1, d, d + 1).filter(n => n > 0 && n < 6)
    localSensor = Map[CNAME, Any]("source" -> false)
    neighboursSensors = Map[CNAME, Map[ID, Any]](
      NBR_RANGE -> (nbrs.toSet[ID].map(nbr => nbr -> Math.abs(d - nbr).toDouble)).toMap
    )
  } yield d -> DeviceState(d, Map.empty[ID, EXPORT], localSensor, neighboursSensors)).toMap
  val sourceId = 2
  state = state + (sourceId -> state(sourceId).copy(localSensors = state(sourceId).localSensors + ("source" -> true)))
  // The following cycle performs the scheduling of rounds and simulates communication by writing on `state`
  val scheduling = devices ++ devices ++ devices ++ devices ++ devices // run 5 rounds each, in a round-robin fashion
  for (d <- scheduling) {
    val ctx = factory.context(
      selfId = d,
      exports = state(d).exports,
      lsens = state(d).localSensors,
      nbsens = state(d).nbrSensors
    )
    println(s"RUN: DEVICE $d\n\tCONTEXT: ${state(d)}")
    val export = program.round(ctx)
    state += d -> state(d).copy(exports = state(d).exports + (d -> export)) // update d's state
    // Simulate sending of messages to neighbours
    state(d)
      .nbrSensors(NBR_RANGE)
      .keySet
      .foreach(nbr => state += nbr -> state(nbr).copy(exports = state(nbr).exports + (d -> export)))
    println(s"\tEXPORT: $export\n\tOUTPUT: ${export.root()}\n--------------")
  }
}*/


import it.unibo.scafi.simulation.frontend.{Launcher, Settings}

object HelloScafi extends Launcher {
  println("BLAH")
  Settings.Sim_ProgramClass = "experiments.GradientProgram"
  Settings.ShowConfigPanel = true
  launch()
}
