package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/1/2016.
  */
// Var probably not the best thing to do here, but use option instead.
case class Config (
                   sampleId: String = "",
                   var setId: String = "",
                   var version: Option[Long] = None,
                   entryFile: String = "",
                   inputFile: Option[String] = None,
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
                   host: String = "http://btllims.broadinstitute.org",
                   port: Int = 9100
                  )
