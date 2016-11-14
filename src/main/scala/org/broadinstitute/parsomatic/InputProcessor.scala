package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger
import scala.io.Source

/**
  * Created by Amr on 9/6/2016.
  */
/**
  *
  * @param inputFile: the input metrics file to be processed.
  */
class InputProcessor(inputFile: String) {
  private val lines = Source.fromFile(inputFile).getLines().filterNot(_.isEmpty()).toList
  private val logger = Logger("InputProcessor")
  logger.info(s"Reading $inputFile...")
  logger.info("Blank lines removed.")
  /**
    *
    * @param start The row/line in the file where the filtering should begin.
    * @param end The row/line(inclusively) in the file where fitlering should end.
    * @return
    */
  def filterByRow(start: Int, end: Int): Either[String, List[String]] = {
    if (end > 0) logger.debug("Parsing by rows " + start + " to " + end)
    else logger.debug("Parsing by rows " + start + " to EOF")
    end match {
      case 0 => Right(lines drop (start - 1))
      case _ if end > start => Right(lines.slice(start - 1, end))
      case _ => Left("filterByRow processing failed.")
    }
  }

  /**
    *
    * @param start The first word in the row where filtering should begin.
    * @param end The first word in the row where filtering should end.
    * @return
    */
  def filterByKey(start: String, end: String): Either[String, List[String]] = {
    if (end.length > 0)
      logger.debug("Parsing by key, starting with " + start + " and ending with " + end)
    else
      logger.debug("Parsing by key, starting with " + start)
    def getKeyRow(lines: List[String], word: String): Int = {
      logger.debug("Searching for key row for keyword " + word)
      val row = lines.indexWhere(_.startsWith(word))
      if (row == -1) {
        logger.debug(word.concat(" not found."))
        row
      } else {
        val shiftRow = row + 1
        logger.debug(word + " found at index " + shiftRow)
        row + 1
      }
    }
    (start, end) match {
      case ("","") => filterByRow(1,0) //This is the same as filtering by row with default settings
      case (x, "") =>
        val keyRow = getKeyRow(lines, x)
        if (keyRow < 0) {
          Left(x.concat(" is not a valid keyword."))
        } else {
          filterByRow(keyRow, 0)
        }
      case ("", y) =>
        val keyRow = getKeyRow(lines, y)
        if (keyRow < 0) {
          Left(y.concat(" is not a valid keyword."))
        } else {
          filterByRow(1, keyRow)
        }
        //filterByRow(1, getKeyRow(lines, y)) //Filter file from first line to first instance of end keyword.
      case (x, y) =>
        val keyRow1 = getKeyRow(lines, x)
        val keyRow2 = getKeyRow(lines, y)
        if ((keyRow1 < 0) && (keyRow2 < 0)) {
          Left(x.concat(" and ").concat(y.concat(" are not a valid keyword pair.")))
        } else {
          filterByRow(keyRow1, keyRow2)
        }
      case _ => Left("filterByKey processing failed.")
    }
  }
}