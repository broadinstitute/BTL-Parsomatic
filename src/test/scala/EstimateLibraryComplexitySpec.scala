import org.broadinstitute.parsomatic.GetPctMultiplexedStat
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class EstimateLibraryComplexitySpec extends FlatSpec with Matchers{
  "EstimateLibraryComplexity" should "return a container with library complexity stats" in {
    val config = Config(
      port = 9101,
      sampleId = "SSF1871C06_PeterNigrovic",
      setId = "Mouse-Nigrovic",
      version = Some(1483633919195L)
    )
  }
}
