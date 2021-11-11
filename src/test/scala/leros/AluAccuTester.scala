package leros

import chisel3._
import chiseltest._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

import leros.Types._

class AluAccuTester(dut: AluAccu) {

  // TODO: this is not the best way look at functions defined as Enum.
  // Workaround would be defining constants

  def alu(a: BigInt, b: BigInt, op: Int): BigInt = {
    val rst: BigInt = op match {
      case 1 => a + b
      case 2 => a - b
      case 3 => a & b
      case 4 => a | b
      case 5 => a ^ b
      case 6 => b
      case 7 => a >> 1
      case _ => -123 // This shall not happen
    }
    rst & BigInt("ffffffff", 16)
  }

  def testOne(a: BigInt, b: BigInt, fun: Int): Unit = {
    dut.io.op.poke(ld)
    dut.io.ena.poke(true.B)
    dut.io.din.poke(a.U)
    dut.clock.step(1)
    dut.io.op.poke(fun.U)
    dut.io.din.poke(b.U)
    dut.clock.step(1)
    dut.io.accu.expect(alu(a, b, fun).U)
  }

  def test(values: Seq[BigInt]) = {
    for (fun <- 1 to 7) {
      for (a <- values) {
        for (b <- values) {
          testOne(a, b, fun)
        }
      }
    }
  }

  // Some interesting corner cases
  val interesting = Array[BigInt](
    1,
    2,
    4,
    123,
    0,
    BigInt("ffffffff", 16),
    BigInt("fffffffe", 16),
    BigInt("80000000", 16),
    BigInt("7fffffff", 16)
  )
  test(interesting)

  val randArgs = Seq.fill(10)(BigInt(scala.util.Random.nextInt) + BigInt("80000000", 16))
  test(randArgs)

}

class AluTesterSpec extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  "AluAccu" should "pass" in {
    test(new AluAccu(32)) { c =>
      new AluAccuTester(c)
    }
  }
}
