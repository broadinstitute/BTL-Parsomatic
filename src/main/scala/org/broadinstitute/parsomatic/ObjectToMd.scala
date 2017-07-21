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

import scala.concurrent.{Await, ExecutionContextExecutor, Future, TimeoutException}
import scala.concurrent.duration._

/**
  * Created by Amr on 9/23/2016.
  */
/**
  *
  * @param setId The analysis/sample_set ID of the sample to be updated/added to MD.
  * @param sampleRef a sampleRef containing a sample ID and sample set ID.
  */

class ObjectToMd(setId: String, sampleRef: SampleRef, test: Boolean, version: Option[Long]){
  var retries = 4
  var port = 9100
  if (test) port = 9101
  val pathPrefix = s"http://btllims.broadinstitute.org:$port/MD"
//  val pathPrefix = "http://gp3c5-33b.broadinstitute.org:9100/MD"
//  val pathPrefix = "http://osiris-pc:9100/MD"
  val metricsUpdate = s"$pathPrefix/metricsUpdate"
  val metricsCreate = s"$pathPrefix/add/metrics"
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  /**
    *
    * @param analysisObject A list of one or more instances of a single MD metrics type.
    * @return
    */
  def run(analysisObject: AnalysisMetrics): Option[HttpResponse] = {
    def doUpdate(au: MetricsUpdate): Option[HttpResponse] = {
      val result = doAnalysisUpdate(au)
      try {
        Some(Await.result(result, 10 seconds))
      } catch {
        case e: TimeoutException =>
          retries match {
            case 0 =>
              None
            case _ =>
              retries = retries - 1
              doUpdate(au)
          }
        case _: Throwable => None
      }
    }
    val analysisUpdate = createAnalysisUpdate(
      setId = setId,
      version = version,
      metricType = MetricsType.withName(analysisObject.getClass.getSimpleName),
      metrics = analysisObject
    )
    doUpdate(analysisUpdate)

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
    * @param setId: a set id to help identify the sample the analysis is related to.
    * @param version: an optional version number for the analysis.
    * @param metricType: a MetricsType object identifying the md metric type.
    * @param metrics: the actual AnalysisMetrics object.
    * @return
    */
  def createAnalysisUpdate(setId: String, version: Option[Long], metricType: MetricsType,
                           metrics: AnalysisMetrics): MetricsUpdate = {
    MetricsUpdate(
      id = setId,
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
