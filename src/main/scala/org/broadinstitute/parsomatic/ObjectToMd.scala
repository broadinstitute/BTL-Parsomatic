package org.broadinstitute.parsomatic
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import org.broadinstitute.MD.types.metrics._
import org.broadinstitute.MD.types.SampleRef
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.stream.ActorMaterializer
import org.broadinstitute.MD.rest._
import org.broadinstitute.MD.types.metrics.MetricsType.MetricsType
import scala.concurrent.Future

/**
  * Created by Amr on 9/23/2016.
  */
/**
  *
  * @param id The sample ID of the sample to be updated/added to MD.
  * @param sampleRef a sampleRef.
  */

class ObjectToMd(id: String, sampleRef: SampleRef, test: Boolean, version: Long){
  var port = 9100
  if (test) port = 9101
  val pathPrefix = s"http://btllims.broadinstitute.org:$port/MD"
  val metricsUpdate = s"$pathPrefix/metricsUpdate"
  val metricsCreate = s"$pathPrefix/add/metrics"
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  /**
    *
    * @param analysisObject A list of one or more instances of a single MD metrics type.
    * @return
    */
  def run(analysisObject: AnalysisMetrics): Future[HttpResponse] = {
    val analysisUpdate = createAnalysisUpdate(
      id = id,
      version = Option(version),
      metricType = MetricsType.withName(analysisObject.getClass.getSimpleName),
      metrics = analysisObject
    )
    doAnalysisUpdate(analysisUpdate)
  }

  /**
    *
    * @param obj an Analysis Update object to be posted to the database.
    * @return
    */
  def doAnalysisUpdate(obj: MetricsUpdate): Future[HttpResponse] = {

    Http().singleRequest(Post(s"$metricsUpdate", HttpEntity(`application/json`, MetricsUpdate.writeJson(obj))))
  }

  /**
    *
    * @param id: a sample id to help identify the sample the analysis is related to.
    * @param version: an optional version number for the analysis.
    * @param metricType: a MetricsType object identifying the md metric type.
    * @param metrics: the actual AnalysisMetrics object.
    * @return
    */
  def createAnalysisUpdate(id: String, version: Option[Long], metricType: MetricsType,
                           metrics: AnalysisMetrics) = {
    MetricsUpdate(
      id = id,
      version = version,
      sampleMetrics = List(new SampleMetrics(sampleRef,
        List(new MetricEntry(
          metricType = metricType,
          metric = metrics
            )
          )
        )
      )
    )
  }
}
