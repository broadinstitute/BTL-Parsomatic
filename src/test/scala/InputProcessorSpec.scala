import org.broadinstitute.parsomatic.InputProcessor
import org.scalatest._

/**
  * Created by Amr on 9/6/2016.
  */

class InputProcessorSpec extends FlatSpec with Matchers {
  val alignmentMetrics = getClass.getResource("/Mouse-A2-single.AlignmentSummaryMetrics.out").getPath
  val notRealFile = "doesnt_exists.txt"
  "A valid input" should "return the 'right' case of an Either" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByRow(1,0).isRight should be (true)
  }
  it should "always return an iterator inclusive of start and end lines indicated by start and end rows" in {
    val ip = new InputProcessor(alignmentMetrics)
    //println(ip.filterByKey("CATEGORY", "PAIR").right)
    ip.filterByKey("CATEGORY", "PAIR").right.map(_.length) should be (Right(4))
  }
  it should "always return an iterator inclusive of start and end lines indicated by start and end keys" in{
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByRow(6,9).right.map(_.length) should be (Right(4))
  }
  it should "return an iterator with # of elements equal to # lines (minus blanks) in file when end row = 0" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByRow(1,0).right.map(_.length) should be (Right(9))
  }
  "An invalid input" should "return a failure string when row input is nonsensical" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByRow(10,1).left.map(_.toString) should be (Left("filterByRow processing failed."))
  }
  it should "return a failure string when start key input doesn't exist in input file" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByKey("FOO", "").left.map(_.toString) should be (Left("FOO is not a valid keyword."))
  }
  it should "return a failure string when end key input doesn't exist in input file" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByKey("", "BAR").left.map(_.toString) should be (Left("BAR is not a valid keyword."))
  }
  it should "return a failure string when both keys don't exist in input file" in {
    val ip = new InputProcessor(alignmentMetrics)
    ip.filterByKey("FOO", "BAR").left.map(_.toString) should be (Left("FOO and BAR are not a valid keyword pair."))
  }
}