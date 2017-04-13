import org.broadinstitute.parsomatic.GetPctMultiplexedStat
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class GetPctMultiplexedStatsSpec extends FlatSpec with Matchers{
  "GetPctMultiplexedStatsSpec" should "return a container with multiplexed Stat" in {
    val config = Config(
      test = true,
      sampleId = "SSF1871C06_PeterNigrovic",
      setId = "Mouse-Nigrovic",
      version = Some(1483633919195L)
    )
    val getter = new GetPctMultiplexedStat(config)
    val foo = getter.getStats
    foo match {
      case Right(r) => r should contain allOf("1.2099902421029955E-4", "pctOfMultiplexedReads")
      case Left(l) => System.exit(1)
    }
  }
}
