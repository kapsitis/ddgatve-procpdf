package lv.ddgatve.procpdf.bookcreator.util

import java.io.InputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

import scala.io.Source._
import lv.ddgatve.procpdf.bookcreator.conf.PptSectionData

/**
 * This class is responsible for reading property file
 */
class PdfProperties(propFileName: String) {

  val lines = fromFile(propFileName).getLines.toList
  val nonemptyLines = lines filter { x => {x.indexOf("=") >= 0 && !x.startsWith("#")}}
  val pairs = nonemptyLines map { x =>
    {
      val strArray = x.split("=")
      if (strArray.size < 2) {
        (strArray(0).trim(), "")
      } else {
        (strArray(0).trim(), strArray(1).trim())
      }
    }
  }

  val pptSections = ((for (i <- 1 to pairs.length) yield {
    val k1 = f"section$i%02d.title"
    val k2 = f"section$i%02d.slides"
    val k3 = f"section$i%02d.overflow"
    val k4 = f"section$i%02d.skip"
    val v1 = PathTemplate.getEntryVal(pairs, k1)
    val v2 = PathTemplate.getEntryVal(pairs, k2)
    val v3 = PathTemplate.getEntryVal(pairs, k3)
    val v4 = PathTemplate.getEntryVal(pairs, k4)
    val vv4 = if (v4.equals("TRUE")) { true } else { false }  
    if (!v1.equals("NA") && !v1.equals("NA") && !v1.equals("NA")) {
      Some(new PptSectionData(i, v1, v2.toInt, v3.toInt, vv4))
    } else { None }
  }) filter (_.isDefined) map (_.get)).toList

  def getPptSections(): List[PptSectionData] = pptSections

}