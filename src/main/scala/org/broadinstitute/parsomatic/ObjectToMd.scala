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
/**
  *
  * @param id
  * @param sampleRef
  */

class ObjectToMd(id: String, sampleRef: SampleRef){
  val pathPrefix = "/MD"
  val analysisUpdate = s"$pathPrefix/analysisUpdate"

  /**
    *
    * @param objList a list of one or more instances of a single MD metrics type
    * @return
    */
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
                case ers: ErccStats =>
                  val analysisUpdate = createAnalysisUpdate(
                    id = id,
                    metricType = MetricsType.ErccStats,
                    metrics = ers
                  )
                  doAnalysisUpdate(analysisUpdate)
                case _ => failureExit("Unrecognized type(s) to load into MD.")
    }
  }

  /**
    *
    * @param obj an Analysis Update object to be posted to the database.
    * @return
    */
  def doAnalysisUpdate(obj: AnalysisUpdate) = {
    Post(s"$analysisUpdate", HttpEntity(`application/json`, AnalysisUpdate.writeJson(obj)))
  }

  /**
    *
    * @param id: a sample id to help identify the sample the analysis is related to
    * @param version: an optional version number for the analysis
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
