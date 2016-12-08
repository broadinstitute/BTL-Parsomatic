package org.broadinstitute.parsomatic
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.broadinstitute.MD.rest.{SampleMetrics, SampleMetricsRequest}
import org.broadinstitute.MD.types.SampleRef
import org.broadinstitute.MD.types.metrics.MetricsType
import org.broadinstitute.MD.types.metrics.MetricsType.MetricsType
import org.broadinstitute.mdreport.Reporters.getSamples
import org.broadinstitute.mdreport.ReporterTraits._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.Await
import org.broadinstitute.MD.types.marshallers.Marshallers._

/**
  * Created by amr on 12/8/2016.
  */
class GetPctDemultiplexedStat(config: Config) extends Samples with Metrics with Requester with MapMaker{
  private implicit lazy val system = ActorSystem()
  private implicit lazy val materializer = ActorMaterializer()
  private implicit lazy val ec = system.dispatcher
  val rootPath = "http://btllims.broadinstitute.org"
  var port = 9100
  if (config.test) port = 9101
  val server = s"$rootPath:$port/MD"
  val path = s"$server/metricsQuery"
  val sampleList: List[String] = getSamples(config.setId, config.version, server)
  val metrics: List[MetricsType] = List(MetricsType.PicardAlignmentSummaryAnalysis)
  val setId = config.setId
  val setVersion = config.version
  val sampleRefs = makeSampleRefs(setId = setId,
    srefs = scala.collection.mutable.ListBuffer[SampleRef]()).toIterator
  val sampleRequests = makeSampleRequests(sr = sampleRefs,
    metrics = metrics,
    sreqs = scala.collection.mutable.ListBuffer[SampleMetricsRequest]())
  val mq = makeMetricsQuery(sampleRequests)
  def getStats():Either[String, List[String]] = {
    /* TODO:
     1 - get the totalReads for all samples for a given setId.
     2 - divide the specified sample's total reads by the total reads for the library.
     3 - generate a result that is a Right(List[String]).
    */

    val query = doQuery(mq)
    val result = query.flatMap(response => Unmarshal(response.entity).to[List[SampleMetrics]])
    val metricsList = Await.result(result, 5 seconds)
    val demultiplexMap: mutable.LinkedHashMap[String, Any] = mutable.LinkedHashMap(
      "PicardAlignmentSummaryAnalysis.PicardAlignmentSummaryMetrics.totalReads" -> None
    )
    val mapsList = fillMap(demultiplexMap, metricsList)
    println(mapsList)
    Left(s"getPctDemultiplexedStat.getStats failed:")
  }
}
