package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/1/2016.
  */
case class Config (sampleId: String = "", sampleSetId: String = "", inputFile: String = "", preset: String = "",
                   headerRow: Int = 1, lastRow: Int = 0, mdType: String = "", byKey: Boolean = false,
                   startKey: String = "", endKey: String = "", var delimiter: String = ",", test: Boolean = false,
                   version: Long = -999)
