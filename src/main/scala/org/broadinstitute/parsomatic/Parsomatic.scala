package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types.SampleRef
import org.broadinstitute.MD.types.SampleRef._


/**
  * Created by amr on 9/1/2016.
  */

object Parsomatic extends App {
  val preset_list = List("PicardAlignmentMetrics", "PicardInsertSizeMetrics", "PicardMeanQualByCycle", "PicardMeanGc",
    "RnaSeqQcStats", "ErccStats")
  val mdType_list = List("PicardAlignmentMetrics", "PicardInserSizetMetrics", "PicardMeanQualByCycle", "PicardMeanGc",
    "RnaSeqQcStats", "ErccStats")
  def parser = {
    new scopt.OptionParser[Config]("Parsomatic") {
      head("Parsomatic", "1.0")
      opt[String]('i', "inputFile").valueName("<file>").required().action((x, c) => c.copy(inputFile = x))
        .text("Path to input file to parse. Required.")
      opt[String]('p', "preset").valueName("<preset>").optional().action((x, c) => c.copy(preset = x))
        .text("Use a parser preset from:".concat(preset_list.toString()))
      opt[String]('m', "mdType").valueName("<type>").optional().action((x, c) => c.copy(mdType = x))
        .text("MD type object to create. Choose from:".concat(mdType_list.toString()))
      opt[Int]('h', "headerRow").valueName("<int>").action((x, c) => c.copy(headerRow = x))
        .text("Header row in file. Do not include blank lines when counting. Default = 1.")
      opt[Int]('l', "lastRow").valueName("<int>").optional().action((x, c) => c.copy(lastRow = x))
        .text("Last row of data to parse. Do not include blank lines when counting. " +
          "If unspecified, will end parse all lines after headerRow.")
      opt[Boolean]('k', "byKey").valueName("<bool>").optional().action((x, c) => c.copy(byKey = x))
        .text("Flag required if processing using key words.")
      opt[String]('s', "startKey").valueName("<string>").optional().action((x, c) => c.copy(startKey = x))
        .text("A key string to indicate header row. Must begin with first character of line.")
      opt[String]('e', "endKey").valueName("<string>").optional().action((x, c) => c.copy(endKey = x))
        .text("A string to indicate last row of data. Must begin with first character of line.")
      opt[String]('d', "delimiter").valueName("<char>").optional().action((x, c) => c.copy(delimiter = x))
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
        case "PicardAlignmentMetrics" => val preset = new Presets.PicardAlignmentMetricPreset(config)
          preset.run()
        case "PicardInsertSizeMetrics" => val preset = new Presets.PicardHistoMetricPreset(config)
          preset.run()
        case "RnaSeqQcStats" => val preset = new Presets.RnaSeqQCPreset(config)
          preset.run()
        case "PicardMeanQualByCycle" => val preset = new Presets.PicardMeanQualByCyclePreset(config)
          preset.run()
        case "ErccStats" => val preset = new Presets.ErccStatsPreset(config)
          preset.run()
        case "PicardMeanGc" => val preset = new Presets.PicardMeanGcPreset(config)
          preset.run()
        case _ => failureExit("Unrecognized preset.")
      }
    } else {
      filterResultHandler(ip.filterByRow(config.headerRow, config.lastRow), config)
    }
  }

  //Though parser.parse will give an error msg if args aren't valid, this forces an exit code of 1 as well.
  def failureExit(msg: String) {
    println("\nFATAL ERROR: " + msg)
    System.exit(1)
  }
/*TODO Should probably validate delimiter somewhere to avoid situation where parsing returns unexpected results
due to delimiter not existing in file.
 */
  def filterResultHandler(result: Either[String, Iterator[String]], config: Config) = {
    result match {
      case Right(filteredResult) =>
        val mapped = new ParsomaticParser(filteredResult, config.delimiter).parseToMap()
        //return a List(MdType(params=value)) object
        //new MapToObjects(config.mdType, mapped).go()
        val obj_list = new MapToObjects(config.mdType, mapped).go()
        val inserter = new ObjectToMd("test_1", SampleRef("sample1", "foo"))
        inserter.run(obj_list)
//        new MapToObjects(config.mdType, mapped).go() match {
//          case asm: List[PicardAlignmentSummaryMetrics] => println("Yup!")
//          case _ => println("Nope!")
//        }
      case Left(unexpectedResult) => failureExit(unexpectedResult)
    }
  }
}