package org.broadinstitute.parsomatic
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import org.broadinstitute.MD.types._
import org.broadinstitute.MD.rest._
import org.broadinstitute.parsomatic.Parsomatic.failureExit
import akka.http.scaladsl.client.RequestBuilding.Post
import org.broadinstitute.MD.rest.MetricsType.MetricsType

/**
  * Created by Amr on 9/23/2016.
  */
class ObjectToMd(id: String, sampleRef: SampleRef){
  val pathPrefix = "/MD"
  val analysisUpdate = s"$pathPrefix/analysisUpdate"

  def doAnalysisUpdate(obj: AnalysisUpdate) = {
    Post(s"$analysisUpdate", HttpEntity(`application/json`, AnalysisUpdate.writeJson(obj)))
  }

  def createAnalysisUpdate(id: String, version: Option[String] = None, metricType: MetricsType, metrics: AnalysisMetrics) = {
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

  def run(objList: Any) = {
    objList.asInstanceOf[List[Any]].head match {
                case asm: PicardAlignmentSummaryMetrics =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.PicardAlignmentSummaryAnalysis,
                    metrics = PicardAlignmentSummaryAnalysis(objList.asInstanceOf[List[PicardAlignmentSummaryMetrics]])
                  )
                  doAnalysisUpdate(analysisUpdate)
                case ism: PicardInsertSizeMetrics =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.PicardInsertSizeMetrics,
                    metrics = ism
                  )
                  doAnalysisUpdate(analysisUpdate)
                case mqc: PicardMeanQualByCycle =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.PicardMeanQualByCycle,
                    metrics = mqc
                  )
                  doAnalysisUpdate(analysisUpdate)
                case rgc: PicardReadGcMetrics =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.PicardReadGcMetrics,
                    metrics = rgc
                  )
                  doAnalysisUpdate(analysisUpdate)
                case rqc: RnaSeqQcStats =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.RnaSeqQcStats,
                    metrics = rqc
                  )
                  doAnalysisUpdate(analysisUpdate)
                case ers: ErccStats => ???
//                  val analysisUpdate = createAnalysisUpdate(
//                    id = id,
//                    metricType = MetricsType.EercStats,
//                    metrics = ers
//                  )
//                  doAnalysisUpdate(analysisUpdate)
                case _ => failureExit("Unrecognized type(s) to load into MD.")
    }
  }
}
