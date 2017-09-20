import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import org.broadinstitute.parsomatic.{Config, ObjectToMd}
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Presets.SampleSheetPreset
import scala.concurrent.duration._
import akka.http.scaladsl.model.StatusCodes._
import org.broadinstitute.MD.types.SampleRef
import org.broadinstitute.MD.types.metrics.SampleSheet
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.language.postfixOps

/**
  * Created by Amr on 9/19/2017.
  * This is a load test meant to test the MD service behavior when receiving many requests.
  */
class MDLoadTestSpec extends FlatSpec with Matchers{
  // val pathPrefix = "http://btllims.broadinstitute.org:9101/MD"
  val pathPrefix = "http://gp3c5-33b.broadinstitute.org:9100/MD"
  //val pathPrefix = "http://Office:9100/MD"
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  private val sampleNames = List("000008542569_C02", "000008542569_D06", "000008542569_D03", "000008542569_G07",
    "000008542569_D08", "000008542569_B11", "000008542569_E04", "000008542569_A09", "000008542569_F04",
    "000008542569_B12", "000008542569_C07", "000008542569_C05", "000008542569_B05", "000008542569_F03",
    "000008542569_F12", "000008542569_E05", "000008542569_A08", "000008542569_F07", "000008542569_A11",
    "000008542569_A03", "000008542569_D01", "000008542569_F05", "000008542569_D11", "000008542569_A02",
    "000008542569_B07", "000008542569_A04", "000008542569_H07", "000008542569_F02", "000008542569_A06",
    "000008542569_C09", "000008542569_A10", "000008542569_B09", "000008542569_A07", "000008542569_H09",
    "000008542569_G09", "000008542569_C08", "000008542569_E01", "000008542569_F08", "000008542569_D07",
    "000008542569_H10", "000008542569_C10", "000008542569_E02", "000008542569_G06", "000008542569_G04",
    "000008542569_A01", "000008542569_H05", "000008542569_H12", "000008542569_D10", "000008542569_B04",
    "000008542569_B01", "000008542569_E12", "000008542569_A05", "000008542569_C12", "000008542569_F06",
    "000008542569_B08", "000008542569_G12", "000008542569_G05", "000008542569_H11", "000008542569_F11",
    "000008542569_H03", "000008542569_B02", "000008542569_H01", "000008542569_D09", "000008542569_H02",
    "000008542569_H04", "000008542569_H08", "000008542569_E03", "000008542569_E06", "000008542569_C03",
    "000008542569_D02", "000008542569_D12", "000008542569_G01", "000008542569_C06", "000008542569_E10",
    "000008542569_C01", "000008542569_E09", "000008542569_G03", "000008542569_A12", "000008542569_C04",
    "000008542569_G02", "000008542569_E08", "000008542569_F01", "000008542569_F09", "000008542569_G08",
    "000008542569_D04", "000008542569_E11", "000008542569_D05", "000008542569_G10", "000008542569_F10",
    "000008542569_C11", "000008542569_H06", "000008542569_G11", "000008542569_B03", "000008542569_B06",
    "000008542569_B10", "000008542569_E07")
  private val url = getClass.getResource("/load_input.tsv")
  private val config = Config(
    sampleId = "Load Test Sample",
    setId = "Load Test Set",
    version = Some(1L),
    inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\target\\scala-2.11\\test-classes\\input_data.tsv ")
  )
  private val r = new SampleSheetPreset(config)
  def doRequest(path: String, json: String): Future[HttpResponse] =
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )
  // create an array of SampleRef objects.
  // Iterate over the array of sampleObjects. Not sure if each iteration will wait until a request is returned or not.
  // If it does, this would not be a good test, we want many requests going in parallel.
  "EntryCreator" should "create a metrics entry" in {
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, s"""{\"id\": \"${config.setId}\", \"version\": ${config.version.get}}""")
    val result = Await.result(request, 5 seconds)
    result.status shouldBe Created
  }
  "Parsomatic" should "add barcodes with InsertSampleSheet preset without any null values occuring" in {
    sampleNames.map(
      x =>{
        val otm = new ObjectToMd(
          setId = config.setId,
          sampleRef = SampleRef(sampleID = x, setID = config.setId),
          test = false,
          version = config.version
        )
        otm.run(SampleSheet(
          sampleName = x, indexBarcode1 = "someBarcode1", indexBarcode2 = "someBarcode2",
          organism = "Test", dataDir = "test")
        )
      }
    )
  }
}
