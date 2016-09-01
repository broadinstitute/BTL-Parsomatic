package org.broadinstitute.parsomatic
import java.io.File

/**
  * Created by amr on 9/1/2016.
  */
case class Config (inputFile: File, headerRow: Int = 1, lastRow: Int, startKey: String, endKey: String,
                   delimiter: String)
