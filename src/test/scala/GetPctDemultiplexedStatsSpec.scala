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
      sampleId = "SSF1871C06_PeterNigrovic",
      setId = "Mouse-Nigrovic",
      version = Some(1483633919195L)
    )
    val getter = new GetPctDemultiplexedStat(config)
    val foo = getter.getStats()
    foo match {
      case Right(r) => r should contain allOf("1.2099902421029955E-4", "pctOfTotalDemultiplexed")
      case Left(l) => println(l)
    }
  }
}
