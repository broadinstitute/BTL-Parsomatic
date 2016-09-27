package org.broadinstitute.parsomatic
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import org.broadinstitute.MD.types._
import org.broadinstitute.MD.rest._
import org.broadinstitute.parsomatic.Parsomatic.failureExit
import akka.http.scaladsl.client.RequestBuilding.Post

/**
  * Created by Amr on 9/23/2016.
  */
class ObjectToMd(id: String, sampleRef: SampleRef){
  val pathPrefix = "/MD"
  val analysisUpdate = s"$pathPrefix/analysisUpdate"

  def doAnalysisUpdate(obj: AnalysisUpdate) = {
    Post(s"$analysisUpdate", HttpEntity(`application/json`, AnalysisUpdate.writeJson(obj)))
  }

  def insert(objList: Any) = {
    objList.asInstanceOf[List[Any]].head match {
                case asm: PicardAlignmentSummaryMetrics =>
                  val analysisUpdate = AnalysisUpdate(
                    id = id,
                    version = None,
                    sampleMetrics = List(new AnalysisUpdate.SampleMetrics(sampleRef,
                      List(new AnalysisUpdate.MetricEntry(
                      metricType = MetricsType.PicardAlignmentSummaryAnalysis,
                      metric = PicardAlignmentSummaryAnalysis(objList.asInstanceOf[List[PicardAlignmentSummaryMetrics]]
                            )
                          )
                        )
                      )
                    )
                  )
                  doAnalysisUpdate(analysisUpdate)
                case ism: PicardInsertSizeMetrics => println("PicardInsertSizeMetrics!")
                case mqc: PicardMeanQualByCycle => println("PicardMeanQualByCycle!")
                case rgc: PicardReadGcMetrics => println("PicardMeanGc!")
                case rqc: RnaSeqQcStats => println("RnaSeqQcStats!")
                case ers: ErccStats => println("ErccStats!")
                case _ => failureExit("Unrecognized type(s) to load into MD.")
    }
  }
}
