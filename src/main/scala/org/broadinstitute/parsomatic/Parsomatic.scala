package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger
import org.broadinstitute.MD.types.SampleRef

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


/**
  * Created by Amr Abouelleil on 9/1/2016.
  */
/**
  * A tool for parsing various types of metrics files and loading their contents into the MD database.
  */
object Parsomatic extends App {
  val presetList = List("PicardAlignmentMetrics", "PicardInsertSizeMetrics", "PicardMeanQualByCycle", "PicardMeanGc",
    "RnaSeqQcStats", "ErccStats")

  def parser = {
    new scopt.OptionParser[Config]("Parsomatic") {
      head("Parsomatic", "1.0")
      opt[String]('i', "sampleId").valueName("<id>").required().action((x,c) => c.copy(sampleId = x))
        .text("The ID of the sample to update metrics for.")
      opt[String]('s', "sampleSetId").valueName("<setId>").required().action((x,c) => c.copy(sampleId = x))
        .text("The ID of the sample set containing the sample to update metrics for.")
      opt[String]('f', "inputFile").valueName("<file>").required().action((x, c) => c.copy(inputFile = x))
        .text("Path to input file to parse. Required.")
      opt[String]('p', "preset").valueName("<preset>").optional().action((x, c) => c.copy(preset = x))
        .text("Use a parser preset from:".concat(presetList.toString()))
      opt[String]('m', "mdType").valueName("<type>").optional().action((x, c) => c.copy(mdType = x))
        .text("MD type object to create. Choose from:".concat(presetList.toString()))
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
    case Some(config) => execute(config)
    case None => failureExit("Please provide valid input.")
  }

  /**
    *
    * @param config The config object that contains all the user-specified arguments to run the program.
    * @return
    */
  def execute(config: Config) = {
    val logger = Logger("Parsomatic.execute")
    logger.debug(config.toString)
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

  /**
    * A method to exit the program with an exit code of 1.
    * Though parser.parse will give an error msg if args aren't valid, this forces an exit code of 1 as well.
    * @param msg A message describing why the program exited unexpectedly.
    */
  def failureExit(msg: String) {
    val logger = Logger("Parsomatic.failureExit")
    logger.error(msg)
    System.exit(1)
  }
/*TODO Should probably validate delimiter somewhere to avoid situation where parsing returns unexpected results
due to delimiter not existing in file.
 */
  /**
    * A method that takes the filteredResult and processes it through the Parsomatic steps.
    * @param result The filteredResult object.
    * @param config The user-supplied arguments.
    * @return
    */
  def filterResultHandler(result: Either[String, Iterator[String]], config: Config) = {
    val logger = Logger("Parsomatic.filterResultHandler")
    result match {
      case Right(filteredResult) =>
        logger.info(config.inputFile + "filtered successfully.")
        val mapped = new ParsomaticParser(filteredResult, config.delimiter).parseToMap()
        logger.info(config.inputFile + "stored to memory successfully.")
        val analysisObject = new MapToAnalysisObject(config.mdType, mapped).go()
        analysisObject match {
          case Right(analysis) =>
            val insertObject = new ObjectToMd(config.sampleId, SampleRef(config.sampleId, config.sampleSetId))
            insertObject.run(analysis) onComplete {
              case Success(s) => logger.info( "Request returned status code " + s.status)
                System.exit(0)
              case Failure(f) => failureExit("Request failed: " + f.getMessage)
            }
          case Left(unexpectedResult) => failureExit(unexpectedResult)
        }
      case Left(unexpectedResult) => failureExit(unexpectedResult)
    }
  }
}