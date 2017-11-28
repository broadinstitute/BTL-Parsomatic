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
import scala.util.{Try, Success, Failure}
import java.io.StringWriter
import java.io.PrintWriter

/**
  * Created by amr on 12/8/2016.
  */
class GetPctMultiplexedStat(config: Config) extends Samples with Metrics with Requester with MapMaker{
  private val logger = Logger("GetPctDemultiplexedStat")
  private implicit lazy val system = ActorSystem()
  private implicit lazy val materializer = ActorMaterializer()
  private implicit lazy val ec = system.dispatcher
  var retries = 4
  val server = s"${config.host}:${config.port}/MD"
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
    query match {
      case Some(r) =>
        val result = Unmarshal(r.entity).to[List[SampleMetrics]]
        val metricsResult = Try(Await.result(result, 300 seconds))
        metricsResult match {
          case Success(metricsList) =>
            val multiplexMap: mutable.LinkedHashMap[String, Any] = mutable.LinkedHashMap(
              "PicardAlignmentSummaryAnalysis.PicardAlignmentSummaryMetrics.totalReads" -> None
            )
            val mapsList = makeMap(multiplexMap.keys.toList, metricsList)
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
                case _ => Left(s"totalReads not populated for ${x.getOrElse("sampleName", "sample")}")
              }
            }
            )
            totals match {
              case l: List[Double] =>
                val libTotals = l.sum
                val percentMultiplexed: Double = (sampleTotal/libTotals) * 100.0
                logger.debug(s"% Multiplex Calculation: ($sampleTotal/$libTotals) * 100.0 = $percentMultiplexed")
                Right(List("pctOfMultiplexedReads", percentMultiplexed.toString))
              case _ => Left("Totals list contains non-Long values.")
            }
          case Failure(e) =>
            val sw = new StringWriter
            logger.error(sw.toString)
            e.printStackTrace(new PrintWriter(sw))
            Left(sw.toString)
        }
      case None => Left("GetPctMultiplexedStats query returned nothing.")
    }
  }
}
