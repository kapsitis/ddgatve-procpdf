package lv.ddgatve.procpdf.bookcreator

import java.io.{ File, FileOutputStream }
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.BaseFont

class BuildStamper(var pdfInFile: String, var pdfOutFile: String) {

  val logger = org.apache.log4j.Logger
    .getLogger(classOf[BuildStamper])

  def stampList(texts: List[String],
    locations: List[List[Int]], colors:List[BaseColor]): Unit = {
    val reader = new PdfReader(pdfInFile)
    val outFile = new File(pdfOutFile)
    val stamper = new PdfStamper(reader, new FileOutputStream(
      outFile))

    var canvas: PdfContentByte = null
    for (itemNum <- 0 until locations.size) {
      canvas = stamper.getOverContent(locations(itemNum)(0))
      val X0 = locations(itemNum)(1)
      val Y0 = locations(itemNum)(2)
      val fontSize = locations(itemNum)(3)
      //val theText = if (itemNum > 0) texts(itemNum) else texts(itemNum)
      val fontType = if (fontSize > 20) Font.BOLD else Font.NORMAL         
      val NEWFONT = if (fontSize > 20) "resources/arialbd.ttf" else "resources/arial.ttf"            
      val bf = BaseFont.createFont(NEWFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
      
      ColumnText.showTextAligned(
        canvas,
        Element.ALIGN_LEFT,
        new Phrase(texts(itemNum), new Font(bf, fontSize, fontType, BaseColor.WHITE)), X0, Y0, 0)

    }
    stamper.close()

  }
}

object BuildStamper {
  def main(args: Array[String]): Unit = {
    val bStamper = new BuildStamper("src/test/resources/updating-policies.pdf",
      "src/test/resources/updating-policies1.pdf")
    bStamper.stampList(List("L4.3"), List(List(1, 70, 709, 35)), 
        List(new BaseColor(41,157,55)))
  }
}

