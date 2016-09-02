package org.broadinstitute.parsomatic
import java.io.File

/**
  * Created by amr on 9/1/2016.
  */

object Parsomatic extends App {
  def parser = {
    new scopt.OptionParser[Config]("Parsomatic") {
      head("Parsomatic", "1.0")
      opt[File]('i', "inputFile").valueName("<file>").required().action((x, c) => c.copy(inputFile = x))
        .text("Path to input file to parse. Required.")
      opt[Int]('h', "headerRow").valueName("<int>").action((x, c) => c.copy(headerRow = x))
        .text("Header row in file. Default = 1.")
      opt[Int]('l', "lastRow").valueName("<int>").optional().action((x, c) => c.copy(lastRow = x))
        .text("Last row of data to parse. If unspecified, will end parse all lines after headerRow.")
      opt[String]('s', "startKey").valueName("<string>").optional().action((x, c) => c.copy(startKey = x))
        .text("A key string to indicate header row. Must begin with first character of line.")
      opt[String]('e', "endKey").valueName("<string>").optional().action((x, c) => c.copy(endKey = x))
        .text("A string to indicate last row of data. Must begin with first character of line.")
      opt[String]('d', "delimiter").valueName("<char>").required().action((x, c) => c.copy(delimiter = x))
        .text("Delimiter used to separate values in file. Use '\\t' for tabs. Required.")
      help("help").text("Prints this help text.")
      note("\nA tool for parsing data files into MD objects.\n")
    }
  }
  private def failureExit(msg: String) {
    println("\nFATAL ERROR: " + msg)
    System.exit(1)
  }

  parser.parse(args, Config(inputFile = new File("NA.tmp"), lastRow = 0, headerRow = 1, startKey = "foo", endKey = "bar", delimiter = "\t")) match {
    // Command line arguments are valid - go execute them
    case Some(config) => println(config.inputFile.toString)
    case None => failureExit("Please provide valid input.")
  }
}