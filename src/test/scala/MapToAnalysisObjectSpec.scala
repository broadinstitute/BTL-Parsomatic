/**
  * Created by Amr on 9/16/2016.
  */
import org.broadinstitute.MD.types.{AnalysisMetrics, PicardAlignmentSummaryAnalysis}
import org.scalatest._
import org.broadinstitute.parsomatic.MapToAnalysisObject

class MapToAnalysisObjectSpec extends FlatSpec with Matchers {
  val test_entry = List(
    Map("CATEGORY" -> "FIRST_OF_PAIR",
      "TOTAL_READS" -> "1184046",
      "PF_READS" -> "1184046",
      "PCT_PF_READS" -> "1",
      "PF_NOISE_READS" -> "0",
      "PF_READS_ALIGNED" -> "0",
      "PCT_PF_READS_ALIGNED" -> "0",
      "PF_ALIGNED_BASES" -> "0",
      "PF_ALIGNED_BASES" -> "0",
      "PF_ALIGNED_BASES" -> "0",
      "PF_HQ_ALIGNED_20_BASES" -> "0",
      "PF_HQ_MEDIAN_MISMATCHES" -> "0",
      "PF_MISMATCH_RATE" -> "0",
      "PF_HQ_ERROR_RATE" -> "0",
      "PF_INDEL_RATE" -> "0",
      "MEAN_READ_LENGTH" -> "25",
      "READS_ALIGNED_IN_PAIRS" -> "0",
      "PCT_READS_ALIGNED_IN_PAIRS" -> "0",
      "BAD_CYCLES" -> "0",
      "STRAND_BALANCE" -> "0",
      "PCT_CHIMERAS" -> "0",
      "PCT_ADAPTER" -> "0.000028"
    )
  )
  "a picard alignment mapper" should "return a PicardAlignmentSummaryAnalysis when given valid input" in {
    val mapper = new MapToAnalysisObject("PicardAlignmentMetrics", test_entry)
    mapper.go().right.get.isInstanceOf[PicardAlignmentSummaryAnalysis] should be(true)
  }
  "a picard alignment mapper" should "give unrecognized MD type message with bad MD type" in {
    val mapper = new MapToAnalysisObject("foo", test_entry)
    mapper.go().left.get should be ("unrecognized mdType input for MapToObject")
  }
}