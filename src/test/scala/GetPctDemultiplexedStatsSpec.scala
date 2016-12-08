import org.broadinstitute.parsomatic.GetPctDemultiplexedStat
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class GetPctDemultiplexedStatsSpec extends FlatSpec with Matchers{
  "GetPctDemultiplexedStatsSpec" should "return a listmap" in {
    val config = Config(
      test = true,
      setId = "SSF-1859",
      version = Some(7)
    )
    val getter = new GetPctDemultiplexedStat(config)
    getter.getStats()

  }
  it should "" in {
    }
}
