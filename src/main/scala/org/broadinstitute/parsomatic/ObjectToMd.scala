package org.broadinstitute.parsomatic
import org.broadinstitute.MD.rest.AnalysisUpdate.MetricEntrySerializer
import org.broadinstitute.MD.types._
import org.broadinstitute.MD.rest._
import org.broadinstitute.parsomatic.Parsomatic.failureExit

/**
  * Created by Amr on 9/23/2016.
  */
class ObjectToMd() {
  def insert(objList: Any) = {
    println(objList)
    objList.asInstanceOf[List[Any]].head match {
                case asm: PicardAlignmentSummaryMetrics =>
                  val pa = PicardAlignmentSummaryAnalysis(objList.asInstanceOf[List[PicardAlignmentSummaryMetrics]])
                  val au = new AnalysisUpdate.MetricEntry(metricType = MetricsType.PicardAlignmentSummaryAnalysis,
                    metric = pa)
                  println(au)
                case ism: PicardInsertSizeMetrics => println("PicardInsertSizeMetrics!")
                case mqc: PicardMeanQualByCycle => println("PicardMeanQualByCycle!")
                case rgc: PicardReadGcMetrics => println("PicardMeanGc!")
                case rqc: RnaSeqQcStats => println("RnaSeqQcStats!")
                case ers: ErccStats => println("ErccStats!")
                case _ => failureExit("Unrecognized type(s) to load into MD.")
    }
  }
}
