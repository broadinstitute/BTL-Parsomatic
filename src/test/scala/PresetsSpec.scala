/**
  * Created by amr on 10/3/2016.
  */

import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.parsomatic.Presets._
import org.broadinstitute.parsomatic.Config

class PresetsSpec extends FlatSpec with Matchers {
  "A SampleSheetPreset" should "produce correct sample sheet data in a db" in {
    val config_1 = Config(
      sampleId = "TestSample1",
      setId = "TestSet1",
      version = Some(1490358021350L),
      inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\input_data.tsv")
    )

    val config_2 = Config(
      sampleId = "TestSample2",
      setId = "TestSet2",
      version = Some(1L),
      inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\input_data.tsv")
    )

    val ssp1 = new SampleSheetPreset(config_1)
    val ssp2 = new SampleSheetPreset(config_2)
    val preset = "SampleSheet"
    val filterResult1 = ssp1.filter(ssp1.start, ssp1.end)
    val filterResult2 = ssp2.filter(ssp2.start, ssp2.end)
    val frLength1 = 7
    val frLength2 = 1
    val ec1_bc1 = "TAAGGCGA"
    val ec1_bc2 = "TATCCTCT"
    val ec2_bc1 = "TAAGGCGA"
    val ec2_bc2 = "GCGTAAGA"
    val org = "human"
    val dp = "/btl/data/walkup/SMART-Seq/SSF-2315/data/HHJTVBGX2/C1-66_2017-03-16_2017-03-16"
    filterResult1.right.get.length shouldBe frLength1
    filterResult2.right.get.length shouldBe frLength2
    }
  "A ErccPreset" should "" in {
    val config = Config(
      sampleId = "ErccPresetTest",
      setId = "TestSet1",
      version = Some(1L),
      //inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\Mouse-A2-single.ErccMetrics.out")
      inputFile = Some("C:\\Dev\\Scala\\Parsomatic\\src\\test\\resources\\new_ercc.out")
    )

    val ep = new ErccStatsPreset(config)
    ep.run()
  }

}
