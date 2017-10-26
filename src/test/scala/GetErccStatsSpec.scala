import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetErccStats
/**
  * Created by amr on 9/29/2016.
  */
class GetErccStatsSpec extends FlatSpec with Matchers{
  "GetErccStats.getStats" should "return a failure message if input stats are wrong length" in {
    val badStats = "TOTAL_READS\tERCC_READS\tUNALIGNED_ERCC_READS\tFRC_GENOME_REF\tFRC_ERCC_READS\tFRC_UNALIGNED_ERCC\n2368092\t75658\t293314\t0.84419\t"
    val badErccStats = GetErccStats.getStats(badStats, "\t")
    badErccStats should be (Left("GetErccStats.getStats failed: Expected input length 6, received 4."))
  }
  it should "return a list with correct input that can be properly mapped to k/v pairs" in {
    val input = "TOTAL_READS\tERCC_READS\tUNALIGNED_ERCC_READS\tFRC_GENOME_REF\tFRC_ERCC_READS\tFRC_UNALIGNED_ERCC\n2368092\t75658\t293314\t0.84419\t0.0319489\t0.123861\n"
    val erccStats = GetErccStats.getStats(input, "\t")
    
    val stat_list = erccStats.right.get
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
