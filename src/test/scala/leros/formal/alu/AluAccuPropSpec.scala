package leros.formal.alu

import org.scalatest.flatspec._
import chiseltest._
import chiseltest.formal._

class AluAccuPropSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "AluAccu"
  it should "satisfy Add" in {
    verify(new AluAccuPropAdd(32), Seq(BoundedCheck(5)))
  }
  it should "satisfy And" in {
    verify(new AluAccuPropAnd(32), Seq(BoundedCheck(5)))
  }
  it should "satisfy all properties" in {
    verify(new AluAccuPropAll(32), Seq(BoundedCheck(5)))
  }
}
