package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.ParsomaticParser


/**
  * Created by amr on 9/1/2016.
  */

object Parsomatic extends App {
  def parser = {
    new scopt.OptionParser[Config]("Parsomatic") {
      head("Parsomatic", "1.0")
      opt[String]('i', "inputFile").valueName("<file>").required().action((x, c) => c.copy(inputFile = x))
        .text("Path to input file to parse. Required.")
      opt[String]('p', "preset").valueName("<preset>").optional().action((x, c) => c.copy(preset = x))
        .text("Use a parser preset from: PicardMetricsParser, PicardHistogramParser")
      opt[String]('m', "mdType").valueName("<type>").optional().action((x, c) => c.copy(mdType = x))
        .text("MD type object to create.")
      opt[Int]('h', "headerRow").valueName("<int>").action((x, c) => c.copy(headerRow = x))
        .text("Header row in file. Default = 1.")
      opt[Int]('l', "lastRow").valueName("<int>").optional().action((x, c) => c.copy(lastRow = x))
        .text("Last row of data to parse. If unspecified, will end parse all lines after headerRow.")
      opt[Boolean]('k', "byKey").valueName("<bool>").optional().action((x, c) => c.copy(byKey = x))
      opt[String]('s', "startKey").valueName("<string>").optional().action((x, c) => c.copy(startKey = x))
        .text("A key string to indicate header row. Must begin with first character of line.")
      opt[String]('e', "endKey").valueName("<string>").optional().action((x, c) => c.copy(endKey = x))
        .text("A string to indicate last row of data. Must begin with first character of line.")
      opt[String]('d', "delimiter").valueName("<char>").required().action((x, c) => c.copy(delimiter = x))
        .text("Delimiter used to separate values in file. Use '\\t' for tabs. Default is comma-separated.")
      help("help").text("Prints this help text.")
      note("\nA tool for parsing data files into MD objects.\n")
    }
  }

  parser.parse(args, Config()
  ) match {
    case Some(config) => execute(config)     // Command line arguments are valid - go execute them
    case None => failureExit("Please provide valid input.") //Exits with code 1
  }

  def execute(config: Config) = {
    val ip = new InputProcessor(config.inputFile)
    if (config.byKey) {
      filterResultHandler(ip.filterByKey(config.startKey, config.endKey), config)
    } else if (config.preset.length() > 0) {
      config.preset match {
        case "PicardMetricPreset" => val filteredResult = new Presets.PicardMetricPreset(config)
          filteredResult.run()
        case "PicardHistoMetricPreset" => val filteredResult = new Presets.PicardHistoMetricPreset(config.inputFile)
          filterResultHandler(filteredResult.run(), config)
        case "RnaSeqQCPreset" => val filteredResult = new Presets.RnaSeqQCPreset(config.inputFile)
          filterResultHandler(filteredResult.run(), config)
      }
    } else {
      filterResultHandler(ip.filterByRow(config.headerRow, config.lastRow), config)
    }
  }

  //Though parser.parse will give an error msg if args aren't valid, this forces an exit code of 1 as well.
  private def failureExit(msg: String) {
    println("\nFATAL ERROR: " + msg)
    System.exit(1)
  }

  def filterResultHandler(result: Either[String, Iterator[String]], config: Config) = {
    result match {
      case Right(filteredResult) => val mapped = new ParsomaticParser(result, config.delimiter).parseToMap()
        new MapToObject(config.mdType,mapped)
      case Left(unexpectedResult) => failureExit(unexpectedResult)
    }
  }
}