import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Parsomatic.validateDelimiter
/**
  * Created by amr on 10/19/2016.
  */
class ParsomaticSpec extends FlatSpec with Matchers{
  "A delimiter validator" should "return true if delim count + 1 equals number of columns(for every row)" in {
    val iter = Iterator("a\tb\tc\td\te", "1\t2\t3\t4\t5", "A\tB\tC\tD\tE")
    val delim = "\t"
    val validation = validateDelimiter(iter, delim)
    validation shouldBe true
  }
  it should "return false if delimiter exists in some lines but not others" in {
    val iter = Iterator("#gobbledeegook", "34,5", "A\tB\tC\tD\tE")
    val delim = "\t"
    val validation = validateDelimiter(iter, delim)
    validation shouldBe false
  }
}
