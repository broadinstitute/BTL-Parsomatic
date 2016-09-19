import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.ParsomaticParser
/**
  * Created by amr on 9/19/2016.
  */
class ParsomaticParserSpec extends FlatSpec with Matchers{
  val iter = Iterator("a\tb\tc\td\te", "1\t2\t3\t4\t5", "A\tB\tC\tD\tE")
  val delim = "\t"
  "A parser should return a list" should "" in {
    val pp = new ParsomaticParser(iter, delim)
    pp.parseToMap() shouldBe a [List[_]]
  }
}
