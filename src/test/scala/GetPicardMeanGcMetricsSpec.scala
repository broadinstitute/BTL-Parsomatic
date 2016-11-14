import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetPicardMeanGcMetrics
/**
  * Created by amr on 9/21/2016.
  */
class GetPicardMeanGcMetricsSpec extends FlatSpec with Matchers{
  private val goodIter = List("GC,Read_Count", "0,5", "1,20", "2,50", "3,10", "4,15")
  private val badIter = List("GC,Read_Count", "0,4", "1,20", "2,x", "3,y", "4,z")
  private val get = GetPicardMeanGcMetrics.getMean(goodIter, ",")
  "GetPicardMeanGcMetrics" should "return a Right of Either with ints as string input" in {
    get.isRight should be (true)
  }
  it should "return a Left of an Either with a failure message" in {
    val badGet = GetPicardMeanGcMetrics.getMean(badIter, ",")
    badGet.isLeft should be (true)
  }
}
