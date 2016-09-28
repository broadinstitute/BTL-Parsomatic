package org.broadinstitute.parsomatic
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import org.broadinstitute.MD.types._
import org.broadinstitute.MD.rest._
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.stream.ActorMaterializer
import org.broadinstitute.MD.rest.MetricsType.MetricsType
import scala.concurrent.Future

/**
  * Created by Amr on 9/23/2016.
  */
/**
  *
  * @param id The sample ID of the sample to be updated/added to MD.
  * @param sampleRef a sampleRef.
  */

class ObjectToMd(id: String, sampleRef: SampleRef){
  val pathPrefix = "http://btllims.broadinstitute.org:9100/MD"
  val analysisUpdate = s"$pathPrefix/analysisUpdate"
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  /**
    *
    * @param analysisObject A list of one or more instances of a single MD metrics type.
    * @return
    */
  def run(analysisObject: AnalysisMetrics): Future[HttpResponse] = {
    val analyisUpdate = createAnalysisUpdate(
      id = id,
      metricType = MetricsType.withName(analysisObject.getClass.getSimpleName),
      metrics = analysisObject
    )
    doAnalysisUpdate(analyisUpdate)
  }

  /**
    *
    * @param obj an Analysis Update object to be posted to the database.
    * @return
    */
  def doAnalysisUpdate(obj: AnalysisUpdate): Future[HttpResponse] = {
    Http().singleRequest(Post(s"$analysisUpdate",
      HttpEntity(`application/json`, AnalysisUpdate.writeJson(obj))))
  }

  /**
    *
    * @param id: a sample id to help identify the sample the analysis is related to.
    * @param version: an optional version number for the analysis.
    * @param metricType: a MetricsType object identifying the md metric type.
    * @param metrics: the actual AnalysisMetrics object.
    * @return
    */
  def createAnalysisUpdate(id: String, version: Option[String] = None, metricType: MetricsType,
                           metrics: AnalysisMetrics) = {
    AnalysisUpdate(
      id = id,
      version = None,
      sampleMetrics = List(new AnalysisUpdate.SampleMetrics(sampleRef,
        List(new AnalysisUpdate.MetricEntry(
          metricType = metricType,
          metric = metrics
            )
          )
        )
      )
    )
  }
}
