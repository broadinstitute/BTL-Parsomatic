import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.GetPicardMeanQualMetrics
/**
  * Created by amr on 9/21/2016.
  */
class GetPicardMeanQualMetricsSpec extends FlatSpec with Matchers{
  "GetPicardMeanQualMetrics" should "return a Right of Either with ints as string input" in {
    val iter = List("CYCLE, MQ", "1,10", "2,20", "3,30", "4, 25")
    val getter = GetPicardMeanQualMetrics.getMeans(iter , ",")
    getter.isRight should be (true)
  }
  it should "return correct r1 and r2 mean values" in {
    val iter = List("CYCLE, MQ", "1,10", "2,20", "3,30", "4, 25")
    val getter = GetPicardMeanQualMetrics.getMeans(iter , ",")
    val result = getter.right
    val means =result.get.toList(1)
    means.split(",")(0).toDouble should be (15.00)
    means.split(",")(1).toDouble should be (27.50)
  }
  it should "return a Left of Either with words as string input" in {
    val iter = List("CYCLE,MQ", "a,b", "c,d", "x,y")
    val getter = GetPicardMeanQualMetrics.getMeans(iter, ",")
    getter.isLeft should be (true)
  }
  it should "return a Right of Either with doubles as string input" in {
    val iter = List("CYCLE, MQ", "1, 3.3", "2, 4.35", "3, 6.6", "4, 5.5")
    val getter = GetPicardMeanQualMetrics.getMeans(iter, ",")
    getter.isRight should be (true)
  }
}
