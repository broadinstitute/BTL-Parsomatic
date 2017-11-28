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
import org.broadinstitute.MD.types.metrics._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.language.postfixOps
import java.util.UUID._
/**
  * Created by Amr on 9/19/2017.
  * This test is meant to test the interactions between Parsomatic and MD, including under the load of many
  * requests.
  */
class MDIntegrationSpec extends FlatSpec with Matchers{
  val pathPrefix = "http://btllims.broadinstitute.org:9101/MD"
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  private val url = getClass.getResource("/load_input.tsv")
  private val config = Config(
    sampleId = "Load Test Sample",
    setId = randomUUID().toString,
    version = Some(1L),
    port = 9101,
    inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\target\\scala-2.11\\test-classes\\input_data.tsv ")
  )
  private val metrics = List(SampleSheet()
//    ,
//      PicardReadGcMetrics(),
//  PicardInsertSizeMetrics(),
//  PicardEstimateLibraryComplexity(),
//  PicardAlignmentSummaryMetrics(),
//  PicardMeanQualByCycle(),
//  DemultiplexedStats(),
//  ErccStats(),
//  RnaSeqQcStats(
//    alignmentMetrics = RnaSeqQcStats.AlignmentMetrics(),
//    annotationMetrics = RnaSeqQcStats.AnnotationMetrics(),
//    covMetrics = RnaSeqQcStats.CovMetrics(),
//    endMetrics = RnaSeqQcStats.EndMetrics(),
//    gapMetrics = RnaSeqQcStats.GapMetrics(),
//    readMetrics = RnaSeqQcStats.ReadMetrics()
//  )
  )
  def doRequest(path: String, json: String): Future[HttpResponse] =
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )
  "EntryCreator" should "create a metrics entry" in {
    val addPath = pathPrefix + "/add/metrics"
    val request = doRequest(addPath, s"""{\"id\": \"${config.setId}\", \"version\": ${config.version.get}}""")
    val result = Await.result(request, 10 seconds)
    result.status shouldBe Created
  }
  "Parsomatic" should "add metrics without any duplicate addresses" in {
    val samples = Seq.fill(1)(randomUUID().toString)
    samples.map(
      x =>{
        val otm = new ObjectToMd(
          setId = config.setId,
          sampleRef = SampleRef(sampleID = x, setID = config.setId),
          host = "http://btllims.broadinstitute.org",
          port = 9101,
          version = config.version
        )
        metrics.map(metric => otm.run(metric))
      }
    )
  }
}
