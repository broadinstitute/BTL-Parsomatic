import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Config
/**
  * Created by amr on 9/29/2016.
  */
class ConfigSpec extends FlatSpec with Matchers{
  val config = Config(
    sampleId = "foo",
    //setId = "bar",
    inputFile = Some("input.txt"),
    preset = "preset",
    delimiter = ","
    )
  "A config" should "have a reassignable delimiter" in {
    config.delimiter = "\t"
    assert(config.delimiter == "\t")
  }
  it should "have byKey as false by default" in {
    config.byKey should be (false)
  }
}
