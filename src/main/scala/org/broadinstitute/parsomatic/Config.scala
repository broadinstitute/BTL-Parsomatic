package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/1/2016.
  */
case class Config (
                   var sampleId: String = "",
                   setId: String = "",
                   var version: Long = -999,
                   entryFile: String = "",
                   inputFile: String = "",
                   preset: String = "",
                   mdType: String = "",
                   headerRow: Int = 1,
                   lastRow: Int = 0,
                   byKey: Boolean = false,
                   startKey: String = "",
                   endKey: String = "",
                   var delimiter: String = ",",
                   validateDelim: Boolean = true,
                   var vOffset: Int = 0,
                   test: Boolean = false
                   )
