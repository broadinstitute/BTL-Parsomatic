import org.broadinstitute.parsomatic.GetPctDemultiplexedStat
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class GetPctDemultiplexedStatsSpec extends FlatSpec with Matchers{
  "GetPctDemultiplexedStatsSpec" should "return a container with demultiplexed Stat" in {
    val config = Config(
      test = true,
      sampleId = "SSF1859B04_A375_AkiYoda",
      setId = "SSF1859",
      version = Some(1479320376921L)
    )
    val getter = new GetPctDemultiplexedStat(config)
    val foo = getter.getStats()
    foo match {
      case Right(r) => r should contain allOf("2.367752052651289", "pctOfTotalDemultiplexed")
      case Left(l) => println(l)
    }

  }
}
