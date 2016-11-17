package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/1/2016.
  */
case class Config (
                   sampleId: String = "",
                   var setId: String = "",
                   var version: Option[Long] = None,
                   entryFile: String = "",
                   inputFile: String = "",
                   preset: String = "",
                   var mdType: String = "",
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
