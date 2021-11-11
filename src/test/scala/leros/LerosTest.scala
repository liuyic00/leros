/*
 * Author: Martin Schoeberl (martin@jopdesign.com)
 *
 * Tester for Leros.
 *
 */

package leros

import chisel3._
import chiseltest._
import org.scalatest.flatspec._

class LerosTest extends AnyFlatSpec with ChiselScalatestTester {
  "Leros" should "pass" in {
    val path  = sys.props.getOrElse("testpath", "asm")
    val progs = leros.shared.Util.getProgs()
    progs.foreach(p => {
      val program = path + "/" + p + ".s"
      println()
      println()
      println("Testing " + program + " in HW simulation")
      test(new Leros(32, 10, program, false)) { dut =>
        var run       = true
        var maxCycles = 100
        while (run) {
          // Looks like peeking elements is gone in Chisel 3, maybe back in ChiselTest?
          //    peek(dut.pc)
          //    peek(dut.accuReg)
          //    peek(dut.instr)
          val pc    = dut.io.dbg.pc.peek.litValue.toInt
          val accu  = dut.io.dbg.acc.peek.litValue.toInt
          val instr = dut.io.dbg.instr.peek().litValue.toInt
          // It is probably NOT a good idea that Predef printf is overloaded in this context
          Predef.printf("pc: 0x%04x instr: 0x%04x accu: 0x%08x\n", pc, instr, accu)
          dut.clock.step(1)
          maxCycles -= 1
          // The following line does not what one expects as peek() == 0.U does not the right thing
          // It is silently false!
          // run = dut.io.dbg.exit.peek() == 0.U && maxCycles > 0
          // run = dut.io.dbg.exit.peek().litValue() === 0 && true
          run = dut.io.dbg.exit.peek().litValue == 0 && maxCycles > 0
          // poke(dut.io.din, maxInstructions)
        }
        val res = dut.io.dbg.acc.expect(0.U, "Accu shall be zero at the end of a test case.\n")
      }
    })
  }
}
