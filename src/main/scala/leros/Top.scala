package leros

import chisel3._
import chisel3.util._

import leros.util._

/** Leros top level without inst memory.
  */
class Top(size: Int, memSize: Int, fmaxReg: Boolean) extends Module {
  val io = IO(new Bundle {
    val dout         = Output(UInt(32.W))
    val dbg          = new Debug
    val instrMemAddr = Output(UInt(memSize.W))
    val instr        = Input(UInt(16.W))
  })

  import State._

  val alu = Module(new AluAccu(size))

  val accu = alu.io.accu

  // The main architectural state
  val pcReg   = RegInit(0.U(memSize.W))
  val addrReg = RegInit(0.U(memSize.W))

  val stateReg = RegInit(feDec)

  switch(stateReg) {
    is(feDec) { stateReg := exe }
    is(exe) { stateReg := feDec }
  }

  val pcNext = WireDefault(pcReg + 1.U)

  // Instruction memory with an address register that is reset to 0
  // val mem = Module(new InstrMem(memSize, prog))
  // for formal verification, move inster memory to outside of cpu

  io.instrMemAddr := pcNext
  val instr = io.instr

  val decReg = RegInit(DecodeOut.default)
  val opdReg = RegInit(0.U(size.W))

  val registerMem  = SyncReadMem(256, UInt(32.W))
  val registerRead = registerMem.read(instr(15, 0))

  // Data memory
  // TODO: shall be byte write addressable
  val dataMem  = SyncReadMem(1 << memSize, UInt(32.W))
  val dataRead = dataMem.read(Mux(decReg.isLoadAddr && stateReg === exe, accu, addrReg))

  // Decode
  val dec = Module(new Decode())
  dec.io.din := instr(15, 8)
  val decout = dec.io.dout

  // Operand
  // TODO: shall we rewrite this to use UInt per default?
  val operand = Wire(SInt(size.W))
  val op16sex = Wire(SInt(16.W))
  op16sex := instr(7, 0).asSInt
  val op24sex = Wire(SInt(24.W))
  op24sex := instr(7, 0).asSInt
  when(decout.nosext) {
    operand := (0.U(24.W) ## instr(7, 0)).asSInt // no sign extension
  }.elsewhen(decout.enahi) {
    operand := (op24sex.asUInt ## accu(7, 0)).asSInt
  }.elsewhen(decout.enah2i) {
    operand := (op16sex.asUInt ## accu(15, 0)).asSInt
  }.elsewhen(decout.enah3i) {
    operand := (instr(7, 0) ## accu(23, 0)).asSInt
  }.otherwise {
    operand := instr(7, 0).asSInt
  }

  // For now do a sequential version of Leros.
  // Later decide where the pipeline registers are placed.

  alu.io.op  := decReg.op
  alu.io.ena := decReg.ena & (stateReg === exe)
  alu.io.din := Mux(decReg.isLoadInd, dataRead, Mux(decReg.isRegOpd, registerRead, opdReg))

  switch(stateReg) {
    is(feDec) {
      decReg := decout
      opdReg := operand.asUInt
    }

    is(exe) {
      pcReg := pcNext
      when(decReg.isStore) {
        registerMem.write(opdReg(15, 0), accu)
      }
      when(decReg.isLoadAddr) {
        addrReg := accu
      }
      when(decReg.isLoadInd) {
        // nothing to be done here
      }
      when(decReg.isStoreInd) {
        dataMem.write(addrReg, accu)
      }
    }

  }

  printf("accu: %x address register: %x\n", accu, addrReg)

  val exit = RegInit(false.B)
  exit := RegNext(decReg.exit)

  println("Generating Leros")
  io.dout := 42.U

  if (fmaxReg) {
    io.dbg.acc   := RegNext(RegNext((accu)))
    io.dbg.pc    := RegNext(RegNext((pcReg)))
    io.dbg.instr := RegNext(RegNext((instr)))
    io.dbg.exit  := RegNext(RegNext((exit)))
  } else {
    io.dbg.acc   := ((accu))
    io.dbg.pc    := ((pcReg))
    io.dbg.instr := ((instr))
    io.dbg.exit  := ((exit))
  }
}

object Top extends App {
  (new chisel3.stage.ChiselStage).emitFirrtl(new Top(32, 10, true), Array("--target-dir", "generated"))
}
