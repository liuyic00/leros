package leros.formal.leros

import leros._
import chisel3._
import chisel3.util._
import chiseltest.formal._

import State._

sealed trait PropGeneral extends Leros {
  switch(past(stateReg)) {
    is(feDec) { assert(stateReg === exe) }
    is(exe) { assert(stateReg === feDec) }
  }
}

class LerosPropGeneral(size: Int, memSize: Int, prog: String, fmaxReg: Boolean)
    extends Leros(size, memSize, prog, fmaxReg)
    with PropGeneral

object TopProp extends App {
  (new chisel3.stage.ChiselStage)
    .emitFirrtl(new LerosPropGeneral(32, 10, "asm/base.s", false), Array("--target-dir", "generated"))
}
