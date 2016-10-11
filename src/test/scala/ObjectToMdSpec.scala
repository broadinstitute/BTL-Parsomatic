import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.ObjectToMd
import org.broadinstitute.MD.types._

import scala.concurrent.duration._
import scala.concurrent.Await
/**
  * Created by Amr on 9/27/2016.
  */
class ObjectToMdSpec extends FlatSpec with Matchers {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  def doRequest(path: String, json: String) =
    Http().singleRequest(
      Post(uri = path, entity = HttpEntity(contentType = `application/json`, string = json))
    )

  val pathPrefix = "http://btllims.broadinstitute.org:9100/MD"

  "ObjectToMd" should "return a 200 OK status code when adding" in {
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, "{\"id\": \"sample_1\", \"version\": \"V1\"}")
    Await.result(request, 5 seconds).status.toString should be("20[1 Created]")
  }
  it should "return a 200 OK status code when updating" in {
    val otm = new ObjectToMd("test", SampleRef("sample_1", "set_1"))
    val request = otm.run(new PicardReadGcMetrics(meanGcContent = 45.55))
    Await.result(request, 5 seconds).status.toString should be("200 OK")
  }
  it should "return a 200 ok status code when deleting" in {
    val delPathPrefix = s"$pathPrefix/delete"
    val path = "foo"
    val id = "bar"
    val version = "V1"
  }
}