import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by Amr on 9/19/2017.
  * This is a load test meant to test the MD service behavior when receiving many requests.
  */
class MDLoadTestSpec extends FlatSpec with Matchers{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  def doRequest(path: String, json: String): Future[HttpResponse] =
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )
  // create an array of SampleRef objects.
  // Iterate over the array of sampleObjects. Not sure if each iteration will wait until a request is returned or not.
  // If it does, this would not be a good test, we want many requests going in parallel.
}
