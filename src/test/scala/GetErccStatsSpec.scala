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
    val badStats = "2368092\n75658\n293314\n0.84419\n".split("\n").toIterator
    val badErccStats = new GetErccStats(badStats)
    badErccStats.getStats should be (Left("GetErccStats.getStats failed."))
  }
  it should "return an iterator with correct input that can be properly mapped to k/v pairs" in {
    val erccStats = new GetErccStats("2368092\n75658\n293314\n0.84419\n0.0319489\n0.123861".split("\n").toIterator)
    val stat_list = erccStats.getStats.right.get.toList
    val header = stat_list.head.split("\t")
    val values = stat_list(1).split("\t")
    val mapped = (header zip values).toMap
    mapped.get("TOTAL_READS") should be (Some("2368092"))
    mapped.get("ERCC_READS") should be (Some("75658"))
    mapped.get("UNALIGNED_ERCC_READS") should be (Some("293314"))
    mapped.get("FRC_GENOME_REF") should be (Some("0.84419"))
    mapped.get("FRC_ERCC_READS") should be (Some("0.0319489"))
    mapped.get("FRC_UNALIGNED_ERCC") should be (Some("0.123861"))
    }
}
