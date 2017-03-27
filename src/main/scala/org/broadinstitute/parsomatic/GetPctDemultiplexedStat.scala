package org.broadinstitute.parsomatic
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import org.broadinstitute.MD.rest.{MetricsQuery, SampleMetrics, SampleMetricsRequest}
import org.broadinstitute.MD.types.SampleRef
import org.broadinstitute.MD.types.metrics.MetricsType
import org.broadinstitute.MD.types.metrics.MetricsType.MetricsType
import org.broadinstitute.mdreport.Reporters.getSamples
import org.broadinstitute.mdreport.ReporterTraits._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.Await
import org.broadinstitute.MD.types.marshallers.Marshallers._
import org.broadinstitute.parsomatic.Parsomatic.failureExit

import scala.language.postfixOps

/**
  * Created by amr on 12/8/2016.
  */
class GetPctDemultiplexedStat(config: Config) extends Samples with Metrics with Requester with MapMaker{
  private val logger = Logger("GetPctDemultiplexedStat")
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
  val setId: String = config.setId
  val setVersion: Option[Long] = config.version
  val sampleRefs: Iterator[SampleRef] = makeSampleRefs(setId = setId,
    srefs = scala.collection.mutable.ListBuffer[SampleRef]()).toIterator
  val sampleRequests: List[SampleMetricsRequest] = makeSampleRequests(sr = sampleRefs,
    metrics = metrics,
    sreqs = scala.collection.mutable.ListBuffer[SampleMetricsRequest]())
  val mq: MetricsQuery = makeMetricsQuery(sampleRequests)
  def getStats:Either[String, List[String]] = {

    val query = doQuery(mq)
    val result = query.flatMap(response => Unmarshal(response.entity).to[List[SampleMetrics]])
    val metricsList = Await.result(result, 5 seconds)
    val demultiplexMap: mutable.LinkedHashMap[String, Any] = mutable.LinkedHashMap(
      "PicardAlignmentSummaryAnalysis.PicardAlignmentSummaryMetrics.totalReads" -> None
    )
    val mapsList = fillMap(demultiplexMap, metricsList)
    // Code review this section. Using Double type because we have to do calculations that lead to a percentage.
    var sampleTotal: Double = 0
    for (m <- mapsList)
      if (m("sampleName") == config.sampleId)
        m.get("PicardAlignmentSummaryAnalysis.PicardAlignmentSummaryMetrics.totalReads") match {
          case Some(t: Long) => sampleTotal = t.toDouble
          case _ => failureExit(s"totalReads not populated for ${m.getOrElse("sampleName", "sample")}")
        }
    val totals = mapsList.map( x =>
    {
      x.get("PicardAlignmentSummaryAnalysis.PicardAlignmentSummaryMetrics.totalReads") match {
        case Some(t: Long) => t.toDouble
        case _ => failureExit(s"totalReads not populated for ${x.getOrElse("sampleName", "sample")}")
      }
    }
    )
    totals match {
      case l: List[Double] =>
        val libTotals = l.sum
        val percentDemultiplexed: Double = (sampleTotal/libTotals) * 100.0
        logger.debug(s"% Demultiplex Calculation: ($sampleTotal/$libTotals) * 100.0 = $percentDemultiplexed")
        Right(List("pctOfTotalDemultiplexed", percentDemultiplexed.toString))
      case _ => Left("Totals list contains non-Long values.")
    }
  }
}
