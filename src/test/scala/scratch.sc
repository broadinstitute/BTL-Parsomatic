import java.io.File
case class Config(foo: Int = -1, out: File = new File("."), xyz: Boolean = false,
                  libName: String = "", maxCount: Int = -1, verbose: Boolean = false, debug: Boolean = false,
                  mode: String = "", files: Seq[File] = Seq(), keepalive: Boolean = false,
                  jars: Seq[File] = Seq(), kwargs: Map[String,String] = Map())

//case class Config (inputFile: File = new File("."), headerRow: Int = 1, lastRow: Int, startKey: String, endKey: String,
//                   delimiter: String, kwargs: Map[String, String] = Map())

def main(args: Array[String]): Unit = {

  val parser = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "3.x")

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

  // parser.parse returns Option[C]
  parser.parse(args, Config()) match {
    case Some(config) =>
    // do stuff

    case None =>
    // arguments are bad, error message will have been displayed
  }
}