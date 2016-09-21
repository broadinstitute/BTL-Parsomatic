import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetPicardMeanQualMetrics
/**
  * Created by amr on 9/21/2016.
  */
class GetPicardMeanQualMetricsSpec extends FlatSpec with Matchers{
  "GetPicardMeanQualMetrics when given correct input " should "return a Right of Either" in {
    val iter = Iterator("L,R", "1,0", "2,20", "3,30")
    val getter = new GetPicardMeanQualMetrics(iter , ",")
    getter.getMeans.isRight should be (true)
  }
}
