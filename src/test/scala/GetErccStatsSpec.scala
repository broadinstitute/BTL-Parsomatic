import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetErccStats
/**
  * Created by amr on 9/29/2016.
  */
class GetErccStatsSpec extends FlatSpec with Matchers{
  val input = "2368092\n75658\n293314\n0.84419\n0.0319489\n0.123861".split("\n").toIterator
  "An input" should "be an iterator with 6 elements" in {
    input.length should be (6)
    }
  "An erccStats" should "contain a ListBuffer with one element" in{
    val erccStats = new GetErccStats(input)
    erccStats.stats.length should be (1)
  }
  it should "have that element be splittable into a six-element object" in {
    val erccStats = new GetErccStats(input)
    erccStats.stats.head.split("\t").length should be (6)
  }
  "GetErccStats.getStats" should "return a failure message if input stats are wrong length" in {
    val badStats = "2368092\n75658\n293314\n0.84419\n0.0319489".split("\n").toIterator
    val badErccStats = new GetErccStats(badStats)
    badErccStats.getStats should be (Left("GetErccStats.getStats failed."))
  }
  it should "return an iterator with correct input that can be properly mapped to k/v pairs" in {
    val erccStats = new GetErccStats(input)
    val stat_iter = erccStats.getStats
    println(stat_iter.)
    stat_iter match {
      case Right(r) =>
        for ((a, b) <- r zip input) {
          println(a,b)
        }
      case Left(l) => println("oops")
    }
  }
}
