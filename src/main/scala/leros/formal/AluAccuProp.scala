package leros.formal

import leros._
import leros.Types._
import chisel3._
import chiseltest.formal._

sealed trait PropGeneral extends AluAccu {
  when(!past(io.ena)) {
    assert(accuReg === past(accuReg))
  }
  assert(io.accu === accuReg)
}
trait PropAdd extends PropGeneral {
  when(past(io.op === add && io.ena)) {
    assert(accuReg === past(accuReg) + past(io.din))
  }
}
trait PropAnd extends PropGeneral {
  when(past(io.op === and && io.ena)) {
    assert(accuReg === (past(accuReg) & past(io.din)))
  }
}

class AluAccuPropGeneral(size: Int) extends AluAccu(size) with PropGeneral
class AluAccuPropAdd(size: Int)     extends AluAccu(size) with PropAdd
class AluAccuPropAnd(size: Int)     extends AluAccu(size) with PropAnd
class AluAccuPropAll(size: Int)     extends AluAccu(size) with PropGeneral with PropAdd with PropAnd

object AluAccuProp extends App {
  (new chisel3.stage.ChiselStage).emitFirrtl(new AluAccuPropAll(32), Array("--target-dir", "generated"))
}
