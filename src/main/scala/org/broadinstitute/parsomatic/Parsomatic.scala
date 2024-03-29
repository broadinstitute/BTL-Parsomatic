package org.broadinstitute.parsomatic
import akka.http.scaladsl.model.StatusCodes
import com.typesafe.scalalogging.Logger
import org.broadinstitute.MD.types.SampleRef
import scala.concurrent.ExecutionContext.Implicits.global
import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success}
import com.lambdaworks.jacks.JacksMapper
import scopt.OptionParser

/**
  * Created by Amr Abouelleil on 9/1/2016.
  */
/**
  * A tool for parsing various types of metrics files and loading their contents into the MD database.
  */
object Parsomatic extends App {

  val presetList = List("PicardAlignmentMetrics", "PicardInsertSizeMetrics", "PicardMeanQualByCycle",
    "PicardMeanGc", "RnaSeqQcStats", "AggregateRnaSeqQcStats", "ErccStats" , "MultiplexedStats",
    "PicardEstimateLibraryComplexity", "SampleSheet")

  def parser: OptionParser[Config] = {

    new scopt.OptionParser[Config]("Parsomatic") {
      head("Parsomatic", "1.2.0")
      opt[String]('i', "sampleId").valueName("<sampleId>").required().action((x,c) => c.copy(sampleId = x))
        .text("The ID of the sample to update metrics for.")
      opt[String]('s', "setId").valueName("<setId>").optional().action((x, c) => c.copy(setId = x))
        .text("The ID of the sample set containing the sample to update metrics for. Supply if not using entrycreator json.")
      opt[Long]('v', "version").valueName("<version>").optional().action((x,c) => c.copy(version = Some(x)))
        .text("Optional version string for the entry.")
      opt[String]('e', "entryFile").optional().action((x, c) => c.copy(entryFile = x))
        .text("If EntryCreator was used you may supply the entry file to pass along sampleId and version.")
      opt[String]('f', "inputFile").valueName("<file>").optional().action((x, c) => c.copy(inputFile = Option(x)))
        .text("Path to input file to parse. Required for all but MultiplexedStats.")
      opt[String]('p', "preset").valueName("<preset>").optional().action((x, c) => c.copy(preset = x))
        .text("Use a parser preset from:".concat(presetList.toString()))
      opt[String]('m', "mdType").valueName("<type>").optional().action((x, c) => c.copy(mdType = x))
        .text("MD type object to create. Required if not using presets. Choose from:".concat(presetList.toString()))
      opt[Int]('h', "headerRow").valueName("<int>").action((x, c) => c.copy(headerRow = x))
        .text("Header row in file. Do not include blank lines when counting. Default = 1.")
      opt[Int]('l', "lastRow").valueName("<int>").optional().action((x, c) => c.copy(lastRow = x))
        .text("Last row of data to parse. Do not include blank lines when counting. " +
          "If unspecified, will end parse all lines after headerRow.")
      opt[Boolean]('k', "byKey").valueName("<bool>").optional().action((x, c) => c.copy(byKey = true))
        .text("Flag required if processing using key words.")
      opt[String]('S', "startKey").valueName("<string>").optional().action((x, c) => c.copy(startKey = x))
        .text("A key string to indicate header row. Must begin with first character of line.")
      opt[String]('E', "endKey").valueName("<string>").optional().action((x, c) => c.copy(endKey = x))
        .text("A string to indicate last row of data. Must begin with first character of line.")
      opt[String]('d', "delimiter").valueName("<char>").optional().action((x, c) => c.copy(delimiter = x))
        .text("Delimiter used to separate values in file. Use '\\t' for tabs. Default is comma-separated.")
      opt[Boolean]('V', "validateDelim").optional().action((x, c) => c.copy(validateDelim = x))
        .text("Validates delimiter. set to 'false' to disable. On by default.")
      opt[Int]('o', "offset").action((x, c) => c.copy(vOffset = x))
        .text("An offset for validation. Should equal the number of headers with no columns. 0 by default.")
      opt[String]('H', "HOST").valueName("<host url>").optional().action((x, c) => c.copy(host = x))
        .text("Optional. Specify database host. Default is http:\\\\btllims.broadinstitute.org.")
      opt[Int]('P', "PORT").valueName("<port>").optional().action((x, c) => c.copy(port = x))
        .text("Optional database host port. Default is 9100. Use 9101 for MdBeta.")
      help("help").text("Prints this help text.")
      note("\nA tool for parsing data files into MD objects.\n")
    }
  }

  parser.parse(args, Config()
  ) match {
    case Some(config) =>
      if (config.entryFile.length > 0) {
        val json = Source.fromFile(config.entryFile).getLines().next()
        val mapper = JacksMapper.readValue[Map[String, String]](json)
        config.setId = mapper("id")
        config.version = Some(mapper("version").toLong)
        if (config.preset != "MultiplexedStats") {
          config.inputFile match {
            case None => failureExit(s"${config.preset} preset requires an --inputFile parameter.")
            case Some(e) => 
          }
        }
      }
      execute(config)
    case None => failureExit("Please provide valid input.")
  }

  /**
    *
    * @param config The config object that contains all the user-specified arguments to run the program.
    * @return
    */
  def execute(config: Config): Unit = {
    val logger = Logger("Parsomatic.execute")
    logger.debug(config.toString)
    if (config.byKey) {
      val ip = new InputProcessor(config.inputFile.get)
      filterResultHandler(ip.filterByKey(config.startKey, config.endKey), config)
    } else if (config.preset.length() > 0) {
      config.preset match {
        case "PicardAlignmentMetrics" => val preset = new Presets.PicardAlignmentMetricPreset(config)
          config.vOffset = 3
          preset.run()
        case "PicardInsertSizeMetrics" => val preset = new Presets.PicardInsertSizeMetric(config)
          config.vOffset = 3
          preset.run()
        case "RnaSeqQcStats" => val preset = new Presets.RnaSeqQCPreset(config)
          preset.run()
        case "AggregateRnaSeqQcStats" => val preset = new Presets.AggregateRnaSeqQCPreset(config)
          preset.run()
        case "PicardMeanQualByCycle" => val preset = new Presets.PicardMeanQualByCyclePreset(config)
          preset.run()
        case "ErccStats" => val preset = new Presets.ErccStatsPreset(config)
          preset.run()
        case "PicardMeanGc" => val preset = new Presets.PicardMeanGcPreset(config)
          preset.run()
        case "PicardEstimateLibraryComplexity" => val preset = new Presets.PicardEstimateLibraryComplexity(config)
          preset.run()
        case "MultiplexedStats" => val preset = new Presets.MultiplexedStatsPreset(config)
          preset.run()
        case "SampleSheet" => val preset = new Presets.SampleSheetPreset(config)
          preset.run()
        case _ => failureExit("Unrecognized preset.")
      }
    } else {
      val ip = new InputProcessor(config.inputFile.get)
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

  def validateDelimiter(filteredLines: Iterator[String], delim: String, offset: Int): Boolean = {
    val logger = Logger("Parsomatic.validateDelimiter")
    //taken from http://stackoverflow.com/questions/12496959/summing-values-in-a-list
    def sum(xs: List[Int]): Int = {
      @tailrec
      def inner(xs: List[Int], accum: Int): Int = {
        xs match {
          case x :: tail => inner(tail, accum + x)
          case Nil => accum
        }
      }
      inner(xs, 0)
    }
    def populateEntries(entry: scala.collection.mutable.ListBuffer[Int]): List[Int] = {
      @tailrec
      def entryAccumulator(entry: scala.collection.mutable.ListBuffer[Int]): List[Int] = {
        if (filteredLines.hasNext) {
          val lineEntries = filteredLines.next.split(delim)
          val entryCount = lineEntries.length
          if (entryCount <= 1) logger.warn(lineEntries.toString + " column(s) found. Is this intended?")
          entry += entryCount
          entryAccumulator(entry)
        } else {
          entry.toList
        }
      }
    entryAccumulator(entry)
    }
    val result: List[Int] = populateEntries(scala.collection.mutable.ListBuffer[Int]())
    val newResult = result.patch(0,Seq(result.head - offset), 1)
    val listSum = sum(newResult)
    val listMult = newResult.head * newResult.length
    if (listSum == listMult) true
    else false
  }
  /**
    * A method that takes the filteredResult and processes it through the Parsomatic steps.
    * @param result The filteredResult object.
    * @param config The user-supplied arguments.
    * @return
    */
  def filterResultHandler(result: Either[String, List[String]], config: Config): Unit = {
    val logger = Logger("Parsomatic.filterResultHandler")
    result match {
      case Right(filteredResult) =>
        logger.info(config.inputFile + " filtered successfully.")
        val resList = filteredResult
        if (config.validateDelim)
          if (!validateDelimiter(resList.to[Iterator], config.delimiter, config.vOffset))
            failureExit("delimiter does not split lines equally.")
        val mapped = new ParsomaticParser(resList.to[Iterator], config.delimiter).parseToMap()
        logger.info(config.inputFile + " stored to memory successfully.")
        val analysisObject = new MapToAnalysisObject(config.mdType, mapped).go()
        analysisObject match {
          case Right(analysis) =>
            val insertObject = new ObjectToMd(config.setId,
              SampleRef(sampleID = config.sampleId, setID = config.setId), host = config.host, port = config.port, config.version)
            insertObject.run(analysis) match {
              case Some(a) =>
                a.status match {
                  case StatusCodes.OK => logger.info("Request successful: " + a.status)
                    System.exit(0)
                  case _ =>
                    val failMsg = "Request failed: " + a.status
                    failureExit(failMsg)
                }
              case None => failureExit("Request failed. Server failed to respond.")
            }
          case Left(unexpectedResult) => failureExit(unexpectedResult)
        }
      case Left(unexpectedResult) => failureExit(unexpectedResult)
    }
  }
}