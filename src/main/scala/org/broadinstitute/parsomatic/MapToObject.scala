package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types._
import scala.collection.mutable.ListBuffer

/**
  * Created by Amr on 9/12/2016.
  */
class MapToObject(input: ListBuffer[Map[String, String]]) {
  for (x <- input) for (y <- x) println(y)
}
