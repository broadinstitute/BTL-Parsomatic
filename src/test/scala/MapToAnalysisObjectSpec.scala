/**
  * Created by Amr on 9/16/2016.
  */
import org.broadinstitute.MD.types._
import org.scalatest._
import org.broadinstitute.parsomatic.MapToAnalysisObject

class MapToAnalysisObjectSpec extends FlatSpec with Matchers {
  val alnTestEntry = List(
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
  val insTestEntry = List(
    Map(
      "MEDIAN_INSERT_SIZE" -> "1",
      "MEDIAN_ABSOLUTE_DEVIATION" -> "1",
      "MIN_INSERT_SIZE" -> "1",
      "MAX_INSERT_SIZE" -> "1",
      "MEAN_INSERT_SIZE" -> "1",
      "STANDARD_DEVIATION" -> "1",
      "READ_PAIRS" -> "1",
      "PAIR_ORIENTATION" -> "F/R",
      "WIDTH_OF_10_PERCENT" -> "1",
      "WIDTH_OF_20_PERCENT" -> "1",
      "WIDTH_OF_30_PERCENT" -> "1",
      "WIDTH_OF_40_PERCENT" -> "1",
      "WIDTH_OF_50_PERCENT" -> "1",
      "WIDTH_OF_60_PERCENT" -> "1",
      "WIDTH_OF_70_PERCENT" -> "1",
      "WIDTH_OF_80_PERCENT" -> "1", 
      "WIDTH_OF_90_PERCENT" -> "1",
      "WIDTH_OF_99_PERCENT" -> "1"
    )
  )
  val mqcTestEntry = List(
    Map(
      "R1_MEAN_QUAL" -> "35",
      "R2_MEAN_QUAL" -> "26"
    )
  )
  "A PicardAlignmentMetrics mapper" should "return a PicardAlignmentSummaryAnalysis when given valid input" in {
    val mapper = new MapToAnalysisObject("PicardAlignmentMetrics", alnTestEntry)
    val metrics = mapper.go()
    metrics.right.get.isInstanceOf[PicardAlignmentSummaryAnalysis]  should be (true)
    metrics match {
      case Right(r) => for (y <- r.toString.split(Array(',', '(', ')')).drop(4)) assert(y.toDouble > -1)
      case Left(l) => println(l)
    }
  }
  it should "give unrecognized MD type message with bad MD type" in {
    val mapper = new MapToAnalysisObject("foo", alnTestEntry)
    mapper.go().left.get should be ("unrecognized mdType input for MapToObject")
  }
  it should "contain negative values to indicate a key for a parameter was not found" in {
    val missingKeys = List(Map("foo" -> "bar"))
    val mapper = new MapToAnalysisObject("PicardAlignmentMetrics", missingKeys)
    val metrics = mapper.go()
    metrics match {
      case Right(r) => for (y <- r.toString.split(Array(',', '(', ')')).drop(4)) assert(y.toDouble < 0)
      case Left(l) => println(l)
    }
  }
  "A PicardInsertSizeMetrics mapper" should "return a PicardInsertSizeMetrics when given valid input" in {
    val mapper = new MapToAnalysisObject("PicardInsertSizeMetrics", insTestEntry)
    val metrics = mapper.go()
    metrics.right.get.isInstanceOf[PicardInsertSizeMetrics]  should be (true)
    metrics match {
      case Right(r) =>
        for (y <- List.concat(r.toString.split(
          Array(',', '(', ')')).slice(4,8), r.toString.split(Array(',', '(', ')')).slice(9,20))) {
        assert(y.toDouble > -1)
      }
      case Left(l) => println(l)
    }
  }
  it should "give unrecognized MD type message with bad MD type" in {
    val mapper = new MapToAnalysisObject("foo", alnTestEntry)
    mapper.go().left.get should be ("unrecognized mdType input for MapToObject")
  }
  it should "contain negative values to indicate a key for a parameter was not found" in {
    val missingKeys = List(Map("foo" -> "bar"))
    val mapper = new MapToAnalysisObject("PicardAlignmentMetrics", missingKeys)
    val metrics = mapper.go()
    metrics match {
      case Right(r) =>
        for (y <- List.concat(r.toString.split(
          Array(',', '(', ')')).slice(4,8), r.toString.split(Array(',', '(', ')')).slice(9,20))) {
        assert(y.toDouble < 0)
      }
      case Left(l) => println(l)
    }
  }
  "A PicardMeanQualByCycle mapper" should "return a PicardMeanQualByCycle when given valid input" in {
    val mapper = new MapToAnalysisObject("PicardMeanQualByCycle", mqcTestEntry)
    val metrics = mapper.go()
    metrics.right.get.isInstanceOf[PicardMeanQualByCycle]  should be (true)
    metrics match {
      case Right(r) => for (y <- r.toString.split(Array(',', '(', ')')).drop(4)) assert(y.toDouble > -1)
      case Left(l) => println(l)
    }
  }
  it should "give unrecognized MD type message with bad MD type" in {
    val mapper = new MapToAnalysisObject("foo", mqcTestEntry)
    mapper.go().left.get should be ("unrecognized mdType input for MapToObject")
  }
  it should "contain negative values to indicate a key for a parameter was not found" in {
    val missingKeys = List(Map("foo" -> "bar"))
    val mapper = new MapToAnalysisObject("PicardAlignmentMetrics", missingKeys)
    val metrics = mapper.go()
    metrics match {
      case Right(r) => for (y <- r.toString.split(Array(',', '(', ')')).drop(4)) assert(y.toDouble < 0)
      case Left(l) => println(l)
    }
  }
}