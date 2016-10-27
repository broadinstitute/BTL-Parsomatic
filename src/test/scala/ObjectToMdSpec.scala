import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.ObjectToMd
import org.broadinstitute.MD.types._
import akka.http.scaladsl.model.StatusCodes._
import org.broadinstitute.MD.types.metrics.PicardReadGcMetrics
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

  val pathPrefix = "http://btllims.broadinstitute.org:9101/MD"
  val id = "parsomatic_unit_test"
  val version = 1
  "ObjectToMd" should "return a CREATED status code when adding" in {
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, s"""{\"id\": \"$id\", \"version\": $version}""")
    Await.result(request, 5 seconds).status shouldBe Created
  }
  it should "return an OK status code when updating" in {
    val otm = new ObjectToMd(id, SampleRef(id, "set_1"), true, version)
    val request = otm.run(new PicardReadGcMetrics(meanGcContent = 45.55))
    val result = Await.result(request, 5 seconds)
    result.status shouldBe OK
  }
  it should "return a 200 ok status code when deleting" in {
    val delPath = s"$pathPrefix/delete/metrics?id=$id&version=$version"
    val request = Http().singleRequest(Post(uri = delPath))
    val result = Await.result(request, 5 seconds)
    result.status shouldBe OK
  }
}