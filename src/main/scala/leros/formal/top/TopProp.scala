package leros.formal.top

import leros._
import chisel3._
import chisel3.util._
import chiseltest.formal._

import State._

sealed trait PropGeneral extends Top {
  switch(past(stateReg)) {
    is(feDec) { assert(stateReg === exe) }
    is(exe) { assert(stateReg === feDec) }
  }
}

class TopPropGeneral(size: Int, memSize: Int, fmaxReg: Boolean) extends Top(size, memSize, fmaxReg) with PropGeneral

object TopProp extends App {
  (new chisel3.stage.ChiselStage).emitFirrtl(new TopPropGeneral(32, 10, false), Array("--target-dir", "generated"))
}
