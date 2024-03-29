import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.ObjectToMd
import org.broadinstitute.MD.types._
import akka.http.scaladsl.model.StatusCodes._
import org.broadinstitute.MD.types.metrics.PicardReadGcMetrics
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.language.postfixOps

/**
  * Created by Amr on 9/27/2016.
  */
class ObjectToMdSpec extends FlatSpec with Matchers {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  def doRequest(path: String, json: String): Future[HttpResponse] =
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )

  val port = 9101
  val host = "http://btllims.broadinstitute.org"
  val pathPrefix = s"$host:$port/MD"

  val set_id = "parsomatic_unit_test"
  val sample_id = "put_sample_1"
  val version = 1
  "ObjectToMd" should "return a CREATED status code when adding" in {
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, s"""{\"id\": \"$set_id\", \"version\": $version}""")
    val result = Await.result(request, 5 seconds)
    result.status shouldBe Created
  }
  it should "return an OK status code when updating" in {
    val otm = new ObjectToMd(set_id, SampleRef(sample_id, set_id), host = host, port = port, Some(version))
    val request = otm.run(PicardReadGcMetrics(meanGcContent = 45.55))
    request match {
      case Some(r) =>
        r.status shouldBe OK
      case None => request shouldBe OK
    }
  }
  it should "return an OK status code when updating a new sample" in {
    val otm = new ObjectToMd(setId = set_id, SampleRef("put_sample_2", set_id), host = host, port = port,  version =Some(version))
    val request = otm.run(PicardReadGcMetrics(meanGcContent = 36.12))
    request match {
      case Some(r) =>
        r.status shouldBe OK
      case None => request shouldBe OK
    }
  }
  it should "return a 200 ok status code when deleting" in {
    val delPath = s"$pathPrefix/delete/metrics?id=$set_id&version=$version"
    val request = Http().singleRequest(Post(uri = delPath))
    val result = Await.result(request, 5 seconds)
    result.status shouldBe OK
  }
}