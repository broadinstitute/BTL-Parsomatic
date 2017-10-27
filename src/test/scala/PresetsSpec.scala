/**
  * Created by amr on 10/3/2016.
  */
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Presets._
import org.broadinstitute.parsomatic.Config
import org.broadinstitute.parsomatic.Parsomatic.filterResultHandler

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class PresetsSpec extends FlatSpec with Matchers {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  def doRequest(path: String, json: String): Future[HttpResponse] =
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )
  "A SampleSheetPreset" should "produce correct sample sheet data in a db" in {
    val config_1 = Config(
      sampleId = "TestSample1",
      setId = "TestSet1",
      version = Some(1490358021350L),
      inputFile = Some("D:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\input_data.tsv")
    )

    val config_2 = Config(
      sampleId = "TestSample2",
      setId = "TestSet2",
      version = Some(1L),
      inputFile = Some("D:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\input_data.tsv")
    )

    val ssp1 = new SampleSheetPreset(config_1)
    val ssp2 = new SampleSheetPreset(config_2)
    val preset = "SampleSheet"
    val filterResult1 = ssp1.filter(ssp1.start, ssp1.end)
    val filterResult2 = ssp2.filter(ssp2.start, ssp2.end)
    val frLength1 = 7
    val frLength2 = 1
    val ec1_bc1 = "TAAGGCGA"
    val ec1_bc2 = "TATCCTCT"
    val ec2_bc1 = "TAAGGCGA"
    val ec2_bc2 = "GCGTAAGA"
    val org = "human"
    val dp = "/btl/data/walkup/SMART-Seq/SSF-2315/data/HHJTVBGX2/C1-66_2017-03-16_2017-03-16"
    filterResult1.right.get.length shouldBe frLength1
    filterResult2.right.get.length shouldBe frLength2
    }
  "A ErccPreset" should "Do stuff" in {
    val ercc_config = Config(
      sampleId = "ErccPresetTest",
      setId = "TestSet1",
      version = Some(1L),
      test = true,
      //inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\Mouse-A2-single.ErccMetrics.out")
      inputFile = Some("D:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\new_ercc.out")
    )
    val pathPrefix = "http://btllims.broadinstitute.org:9101/MD"
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, s"""{\"id\": \"${ercc_config.setId}\", \"version\": ${ercc_config.version.get}}""")
    val result = Await.result(request, 10.seconds)
    val ep = new ErccStatsPreset(ercc_config)
    val filtered = ep.filter(1, 0)
    filtered.right.get.length shouldBe 2
    filtered.right.get.head.split(',').length shouldBe 6
  }

}
