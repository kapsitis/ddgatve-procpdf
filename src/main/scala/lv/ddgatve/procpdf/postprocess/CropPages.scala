package lv.ddgatve.procpdf.postprocess

import java.io.FileOutputStream
import java.io.IOException
import com.itextpdf.text.DocumentException
import com.itextpdf.text.pdf.PdfDictionary
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfRectangle
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.PageSize

/**
 * Crude page cropping - the page size is reduced after the crop
 */
object CropPages {
  def manipulatePdf(src: String, dest: String): Unit = {
    val widthA4 = PageSize.A4.getWidth
    val heightA4 = PageSize.A4.getHeight
    val x0 = 25.0f
    //    val y0 = x0 * (heightA4/widthA4)
    val y0 = 25.0f
    val width = widthA4 - x0
    val height = heightA4 - y0
    println("width = " + width + ", height = " + height)
    val reader = new PdfReader(src)
    val n: Int = reader.getNumberOfPages()
    var pageDict: PdfDictionary = null
    val rect = new PdfRectangle(x0, y0, width, height)
    for (i <- 1 to n) {
      pageDict = reader.getPageN(i)
      pageDict.put(PdfName.CROPBOX, rect)
    }
    val stamper = new PdfStamper(reader, new FileOutputStream(dest))
    stamper.close()
    reader.close()
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: CropPages src.pdf dest.pdf")
      System.err.println("  2 command-line arguments required")
      System.exit(0)
    }
    manipulatePdf(args(0), args(1))
  }
}
 
