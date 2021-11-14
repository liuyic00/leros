package leros.formal

import org.scalatest.flatspec._
import chiseltest._
import chiseltest.formal._

import leros._

/** Verify Leros with instr in instr-mem.
  *
  * Used to test ChiselTest formal verification part.
  */
class LerosPropSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "Leros"
  it should "satisfy general" in {
    verify(new LerosPropGeneral(32, 10, "asm/base.s", false), Seq(BoundedCheck(1)))
  }
}
