package org.broadinstitute.parsomatic
import java.io.File

/**
  * Created by amr on 9/1/2016.
  */

object Parsomatic extends App{
  def parser = {
    new scopt.OptionParser[Config]("Parsomatic") {
    head("Parsomatic", "1.0")
    opt[File]('f', "inputFile").required().valueName("<file>").action((x,c) => c.copy(inputFile = x)).
      text("Input file to parse.")
    opt[Int]('h', "headerRow").action((x,c) => c.copy(headerRow = x)).text("Header row in file. Default = 1.")
    opt[Int]('l', "lastRow").action((x,c) => c.copy(lastRow = x)).text("Last row of data to parse.")
    opt[String]('s', "startKey").action((x,c) => c.copy(startKey = x)).text("A key string to indicate header row.")
    opt[String]('e', "endKey").action((x,c) => c.copy(endKey = x)).text("A string to indicate last row of data.")
    opt[String]('d', "delimiter").required().action((x, c) => c.copy(delimiter = x)).
      text("Delimiter used to separate values in file.")
    help("help").text("Prints this help text.")
    }
  }
//  parser.parse(args, Config(inputFile = )) match {
//    case Some(config) => //do stuff
//  }
}
