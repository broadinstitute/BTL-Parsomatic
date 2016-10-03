import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.ObjectToMd
import org.broadinstitute.MD.types._
import scala.concurrent.duration._
import scala.concurrent.Await
/**
  * Created by Amr on 9/27/2016.
  */
class ObjectToMdSpec extends FlatSpec with Matchers{

  "ObjectToMd" should "return a 200 OK status code" in {
    val otm = new ObjectToMd("test", SampleRef("sample_1", "set_1"))
    val request = otm.run(new PicardReadGcMetrics(meanGcContent = 45.55))
    Await.result(request, 5 seconds).status.toString should be ("200 OK")
  }
}
