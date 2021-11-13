package leros.formal.top

import org.scalatest.flatspec._
import chiseltest._
import chiseltest.formal._

class TopPropSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "Top"
  it should "satisfy general" in {
    verify(new TopPropGeneral(32, 10, false), Seq(BoundedCheck(1)))
  }
}
