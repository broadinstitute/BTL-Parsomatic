import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetPicardMeanGcMetrics
/**
  * Created by amr on 9/21/2016.
  */
class GetPicardMeanGcMetricsSpec extends FlatSpec with Matchers{
  val goodIter = Iterator("GC,Read_Count", "0,5", "1,20", "2,50", "3,10", "4,15")
  val badIter = Iterator("GC,Read_Count", "0,4", "1,20", "2,x", "3,y", "4,z")
  val get = new GetPicardMeanGcMetrics(goodIter, ",")
  "GetPicardMeanGcMetrics" should "return a Right of Either with ints as string input" in {
    get.getMean.isRight should be (true)
  }
  it should "return a Left of an Either with a failure message" in {
    val badGet = new GetPicardMeanGcMetrics(badIter, ",")
    badGet.getMean.isLeft should be (true)
  }
  "convertAndMultiply" should "convert a comma-sep string of 2 ints into the product of the two." in {
    val nums = "5,10"
    get.convertAndMultiply(nums) should be (50)
  }
}
