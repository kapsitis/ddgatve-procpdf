package lv.ddgatve.procpdf.postprocess

import java.io.{ FileOutputStream, IOException }
import com.itextpdf.text.{ Document, PageSize, Rectangle }
import com.itextpdf.text.pdf.{ PdfContentByte, PdfImportedPage, PdfReader, PdfWriter }
import scala.xml._

object PdfScalingWriter {

  /**
   * Utility to create a standardized version of some scanning result with
   * each page properly rotated.
   */
  def main(args: Array[String]): Unit = {
    if (args.size < 1) {
      Console.err.println("Usage: PdfCorrectingWriter <pdf-file>.UL.PDF")
      System.exit(0)
    }
    val inFile = args(0)
    val outFile = inFile.substring(0, inFile.length - 7) + ".clean.pdf"

    val document = new Document()

    println("A4.getHeight() = " + PageSize.A4.getHeight)
    println("leftMargin = " + document.leftMargin)
    println("rightMargin = " + document.rightMargin)
    println("topMargin = " + document.topMargin)
    println("bottomMargin = " + document.bottomMargin)

    val reader = new PdfReader(inFile)
    val numPages = reader.getNumberOfPages
    println("numpages = " + numPages)
    val W = PageSize.A4.getWidth
    val H = PageSize.A4.getHeight

    val writer = PdfWriter.getInstance(document, new FileOutputStream(outFile))
    document.setPageSize(PageSize.A4)
    document.setMargins(36f, 36f, 36f, 36f)
    document.open

//    val rect = new Rectangle(W/10,H/10,9*W/10,9*H/10)
//    writer.setCropBoxSize(rect)
//
    
    val cb: PdfContentByte = writer.getDirectContent
    val scaleRatio = 1.15f


    for (i <- 1 to numPages) {
      val imported = writer.getImportedPage(reader, i)

      val a = scaleRatio
      val b = 0
      val c = 0
      val d = scaleRatio
      val e = 0
      val f = H*(1-scaleRatio)

      cb.addTemplate(imported, a, b, c, d, e, f)
      document.newPage
    }
    document.close
  }
}