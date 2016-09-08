import org.broadinstitute.parsomatic.FilterPresets._
import org.scalatest._

/**
  * Created by Amr on 9/6/2016.
  */

class FilterPresetsSpec extends FlatSpec with Matchers {
  val alignmentMetrics = "C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\Mouse-A2-single.AlignmentSummaryMetrics.out"
  val insertMetrics = "C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\Mouse-A2-single.InsertSize.out"
  val gcMetrics = "C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\Mouse-A2-single.ReadGCMetrics.out"

  "A PicardMetricPreset" should "return a header row as first element of iterable from alignment summary file" in {
    val filteredAlignment = new PicardMetricPreset(alignmentMetrics)
    val run = filteredAlignment.run()
    filteredAlignment.start should be (7)
    run.isRight should be (true)
  }
  it should "return a FailureExit" in {

  }

//  "A PicardHistMetricPreset" should "return an iterable representation of a file" in {
//    val parsedInsert = new PicardMetricPreset(insertMetrics)
//    val parsedGc = new PicardMetricPreset(gcMetrics)
//  }
}