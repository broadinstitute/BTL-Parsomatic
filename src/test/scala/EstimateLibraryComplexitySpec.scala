import org.broadinstitute.parsomatic.GetPctDemultiplexedStat
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class EstimateLibraryComplexitySpec extends FlatSpec with Matchers{
  "GetPctDemultiplexedStats" should "return a container with demultiplexed Stat" in {
    val config = Config(
      test = true,
      sampleId = "SSF1871C06_PeterNigrovic",
      setId = "Mouse-Nigrovic",
      version = Some(1483633919195L)
    )
    val getter = new GetPctDemultiplexedStat(config)
    val foo = getter.getStats()
//    foo match {
//      case Right(r) => r should contain allOf(???, ???)
//      case Left(l) => println(l)
//    }
  }
}
